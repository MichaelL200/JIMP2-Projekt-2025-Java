package graphdivider.model;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.github.fommil.netlib.ARPACK;
import org.netlib.util.doubleW;
import org.netlib.util.intW;
import java.util.Arrays;

// Utility class for spectral partitioning of graphs.
public final class GraphEigenvalues
{
    private static final Logger LOGGER = Logger.getLogger(GraphEigenvalues.class.getName());

    public static class EigenResult
    {
        public final double[] eigenvalues;
        public final double[][] eigenvectors;

        public EigenResult(double[] values, double[] vectors, int n, int p)
        {
            this.eigenvalues = values;
            this.eigenvectors = new double[p][n];
            for (int i = 0; i < p; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    this.eigenvectors[i][j] = vectors[j * p + i];
                }
            }
        }
    }

    private GraphEigenvalues()
    {
    }

    // Computes the Laplacian matrix (L = D - A) in CSR format for the given graph model.
    public static CSRmatrix toLaplacianCSRmatrix(GraphModel model)
    {
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();

        int maxVertex = Arrays.stream(adjacencyList).max().orElse(-1);
        int size = maxVertex + 1;

        Map<Integer, Set<Integer>> neighborsMap = new HashMap<>();
        for (int i = 0; i < adjacencyPointers.length; i++)
        {
            int start = adjacencyPointers[i];
            int end = (i + 1 < adjacencyPointers.length) ? adjacencyPointers[i + 1] : adjacencyList.length;
            int vertex = adjacencyList[start];
            neighborsMap.putIfAbsent(vertex, new HashSet<>());
            for (int j = start + 1; j < end; j++)
            {
                int neighbor = adjacencyList[j];
                if (neighbor != vertex)
                {
                    neighborsMap.get(vertex).add(neighbor);
                    neighborsMap.putIfAbsent(neighbor, new HashSet<>());
                    neighborsMap.get(neighbor).add(vertex);
                }
            }
        }

        int[] rowPtr = new int[size + 1];
        List<Integer> colIndList = new ArrayList<>();
        List<Integer> valuesList = new ArrayList<>();
        int idx = 0;

        for (int i = 0; i < size; i++)
        {
            rowPtr[i] = idx;
            Set<Integer> neighbors = neighborsMap.getOrDefault(i, Collections.emptySet());
            colIndList.add(i);
            valuesList.add(neighbors.size());
            idx++;
            for (int neighbor : neighbors)
            {
                if (neighbor == i) continue;
                colIndList.add(neighbor);
                valuesList.add(-1);
                idx++;
            }
        }
        rowPtr[size] = idx;

        int[] colInd = colIndList.stream().mapToInt(Integer::intValue).toArray();
        int[] values = valuesList.stream().mapToInt(Integer::intValue).toArray();

        return new CSRmatrix(rowPtr, colInd, values, size);
    }

    // Logs Laplacian matrix info for debugging.
    private static void logLaplacianInfo(int size, int nnz, int[] rowPtr, int[] colInd, int[] values)
    {
        if (LOGGER.isLoggable(Level.FINE))
        {
            LOGGER.fine("Laplacian CSR matrix: " + size + "x" + size + ", " + nnz + " non-zero values");
            // Only print full arrays for small graphs
            if (size <= 100)
            {
                LOGGER.fine("Row pointers: " + Arrays.toString(rowPtr));
                LOGGER.fine("Column indices: " + Arrays.toString(colInd));
                LOGGER.fine("Values: " + Arrays.toString(values));
            } else
            {
                LOGGER.fine("Row pointers: [length=" + rowPtr.length + "]");
                LOGGER.fine("Column indices: [length=" + colInd.length + "]");
                LOGGER.fine("Values: [length=" + values.length + "]");
            }
        }
    }

    // Computes the smallest p eigenpairs of the Laplacian matrix using ARPACK.
    public static EigenResult computeSmallestEigenpairs(CSRmatrix laplacian, int p) throws Exception
    {
        System.out.println("ARPACK implementation: " + ARPACK.getInstance().getClass().getName());

        int n = laplacian.size();
        if (n <= 0 || p <= 0 || p > n)
        {
            throw new IllegalArgumentException("Invalid input: n = " + n + ", p = " + p + ". Ensure n > 0 and 0 < p <= n.");
        }

        ARPACK arpack = ARPACK.getInstance();

        // ARPACK parameters
        int ncv = Math.min(4 * p, n); // Subspace dimension
        int maxIter = 10000;
        double tol = 1e-10; // Stricter tolerance for higher precision

        // ARPACK internal variables
        intW ido = new intW(0);
        intW info = new intW(0);
        String bmat = "I";   // Standard eigenvalue problem
        String which = "SM"; // Smallest magnitude

        double[] resid = new double[n];
        Arrays.fill(resid, 0.0); // Ensure deterministic initialization
        double[] V = new double[n * ncv];
        int ldv = n;
        int[] iparam = new int[11];
        int[] ipntr = new int[11];
        double[] workd = new double[3 * n];
        int lworkl = 3 * ncv * (ncv + 2);
        double[] workl = new double[lworkl];

        // Setup iparam
        iparam[0] = 1;       // Exact shifts
        iparam[2] = maxIter; // Max iterations
        iparam[6] = 1;       // Mode 1: standard eigenproblem Ax = lambda x

        // Reverse communication loop
        while (ido.val != 99)
        {
            doubleW tolWrapper = new doubleW(tol);
            arpack.dsaupd(ido, bmat, n, which, p, tolWrapper, resid, ncv, V, ldv, iparam, ipntr, workd, workl, lworkl, info);

            if (ido.val == -1 || ido.val == 1)
            {
                double[] x = workd;
                double[] y = new double[n];
                int xOffset = ipntr[0] - 1;
                int yOffset = ipntr[1] - 1;

                for (int i = 0; i < n; i++) y[i] = 0.0;

                for (int i = 0; i < n; i++)
                {
                    for (int j = laplacian.getRowPtr()[i]; j < laplacian.getRowPtr()[i + 1]; j++)
                    {
                        y[i] += laplacian.getValues()[j] * x[xOffset + laplacian.getColInd()[j]];
                    }
                }
                System.arraycopy(y, 0, workd, yOffset, n);

            }
        }

        if (info.val != 0)
        {
            throw new RuntimeException("ARPACK dsaupd error: " + info.val);
        }

        // Retrieve eigenpairs
        boolean[] select = new boolean[ncv];
        double[] d = new double[p];
        double[] Z = new double[n * p];

        intW pWrapper = new intW(p);
        arpack.dseupd(true, "A", select, d, Z, n, 0.0, bmat, n, which, pWrapper, tol, resid, ncv, V, ldv, iparam, ipntr, workd, workl, lworkl, info);

        if (info.val != 0)
        {
            throw new RuntimeException("ARPACK dseupd error: " + info.val);
        }

        // Normalize eigenvectors
        for (int i = 0; i < p; i++)
        {
            double norm = 0.0;
            for (int j = 0; j < n; j++)
            {
                norm += Z[j * p + i] * Z[j * p + i];
            }
            norm = Math.sqrt(norm);
            if (norm > 0)
            {
                for (int j = 0; j < n; j++)
                {
                    Z[j * p + i] /= norm;
                }
            }
        }

        return new EigenResult(d, Z, n, p);
    }

    // Prints each eigenvalue followed by its corresponding eigenvector.
    public static void printEigenData(GraphEigenvalues.EigenResult eigenResult)
    {
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_RESET = "\u001B[0m";

        for (int i = 0; i < eigenResult.eigenvalues.length; i++)
        {
            System.out.print(ANSI_CYAN + "\tEigenvalue: ");
            System.out.println(String.format("\t%.3f", eigenResult.eigenvalues[i]) + ANSI_RESET);

            System.out.print(ANSI_CYAN + "\tEigenvector: [");
            for (int j = 0; j < eigenResult.eigenvectors[i].length; j++)
            {
                System.out.print(String.format("\t%.3f", eigenResult.eigenvectors[i][j]));
                if (j < eigenResult.eigenvectors[i].length - 1) System.out.print(", ");
            }
            System.out.println("]" + ANSI_RESET);
        }
    }
}