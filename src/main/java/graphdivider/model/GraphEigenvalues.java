package graphdivider.model;

import com.github.fommil.netlib.ARPACK;
import org.netlib.util.doubleW;
import org.netlib.util.intW;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Spectral partitioning utilities for graphs.
 * Provides methods for computing eigenvalues and eigenvectors using ARPACK.
 */
public final class GraphEigenvalues
{
    // Logger for debug/info messages
    private static final Logger LOGGER = Logger.getLogger(GraphEigenvalues.class.getName());

    /**
     * Holds eigenvalues and eigenvectors.
     */
    public static class EigenResult
    {
        // Array of computed eigenvalues
        public final double[] eigenvalues;
        // 2D array of eigenvectors (eigenvectors[i][j] = j-th component of i-th eigenvector)
        public final double[][] eigenvectors;

        /**
         * Converts a flat vector array to a 2D array of eigenvectors.
         *
         * @param values Array of eigenvalues.
         * @param vectors Flat array of eigenvectors (column-major).
         * @param n Number of rows (vector length).
         * @param p Number of eigenpairs.
         */
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

    // Prevent instantiation of utility class
    private GraphEigenvalues() {}

    /**
     * Computes the smallest p eigenpairs of the Laplacian matrix using ARPACK.
     * Returns eigenvalues and eigenvectors.
     *
     * @param laplacian Laplacian matrix in CSR format.
     * @param p Number of smallest eigenpairs to compute.
     * @return EigenResult containing eigenvalues and eigenvectors.
     * @throws Exception if ARPACK fails or input is invalid.
     */
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
        double tol = 1e-6; // Stricter tolerance

        // ARPACK internal variables
        intW ido = new intW(0);
        intW info = new intW(0);
        String bmat = "I";   // Standard eigenvalue problem
        String which = "SM"; // Smallest magnitude

        double[] resid = new double[n];
        Arrays.fill(resid, 0.0); // Deterministic init
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
        iparam[6] = 1;       // Mode 1: standard eigenproblem

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

                // y = L * x
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

    /**
     * Prints eigenvalues and eigenvectors to the console.
     * Uses ANSI color codes for readability.
     *
     * @param eigenResult The EigenResult to print.
     */
    public static void printEigenData(GraphEigenvalues.EigenResult eigenResult)
    {
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_RESET = "\u001B[0m";

        System.out.println(ANSI_CYAN + "\t\tEIGENPAIRS" + ANSI_RESET);
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