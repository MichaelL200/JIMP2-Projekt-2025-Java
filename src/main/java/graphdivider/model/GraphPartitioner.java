package graphdivider.model;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.github.fommil.netlib.ARPACK;
import org.netlib.util.doubleW;
import org.netlib.util.intW;
import java.util.Arrays;

// Utility class for spectral partitioning of graphs.
public final class GraphPartitioner
{
    private static final Logger LOGGER = Logger.getLogger(GraphPartitioner.class.getName());

    private GraphPartitioner()
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

        logLaplacianInfo(size, idx, rowPtr, colInd, values);

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
    public static double[][] computeSmallestEigenpairs(CSRmatrix laplacian, int p) throws Exception
    {
        int n = laplacian.size();
        ARPACK arpack = ARPACK.getInstance();

        // ARPACK parameters
        int ncv = Math.min(2 * p + 1, n); // Subspace dimension
        int maxIter = 1000;
        double tol = 1e-6;

        // ARPACK internal variables
        intW ido = new intW(0);
        intW info = new intW(0);
        String bmat = "I";   // Standard eigenvalue problem
        String which = "SM"; // Smallest magnitude

        double[] resid = new double[n];
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
            doubleW tolWrapper = new doubleW(tol); // Wrap the existing 'tol' variable
            arpack.dsaupd(ido, bmat, n, which, p, tolWrapper, resid, ncv,
                    V, ldv, iparam, ipntr, workd, workl, lworkl, info);

            if (ido.val == -1 || ido.val == 1)
            {
                // Perform y = A * x using CSR format
                double[] x = Arrays.copyOfRange(workd, ipntr[0] - 1, ipntr[0] - 1 + n);
                double[] y = new double[n];

                for (int i = 0; i < n; i++)
                {
                    for (int j = laplacian.getRowPtr()[i]; j < laplacian.getRowPtr()[i + 1]; j++)
                    {
                        y[i] += laplacian.getValues()[j] * x[laplacian.getColInd()[j]];
                    }
                }
                System.arraycopy(y, 0, workd, ipntr[1] - 1, n);
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

        intW pWrapper = new intW(p); // Wrap the 'p' parameter in an intW object
        arpack.dseupd(true, "A", select, d, Z, n, 0.0, bmat, n, which,
                pWrapper, tol, resid, ncv, V, ldv, iparam, ipntr, workd, workl, lworkl, info);

        if (info.val != 0)
        {
            throw new RuntimeException("ARPACK dseupd error: " + info.val);
        }

        return new double[][]{d, Z};
    }
}
