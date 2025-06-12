package graphdivider.model;

/**
 * Sparse matrix in Compressed Sparse Row (CSR) format.
 * Used for efficient storage and operations on sparse graphs.
 *
 * @param rowPtr Row pointers array.
 * @param colInd Column indices array.
 * @param values Values array.
 * @param size Number of rows (and columns, for square matrices).
 */
public record CSRmatrix(int[] rowPtr, int[] colInd, int[] values, int size)
{
    /**
     * Gets the value at the specified (row, col) position.
     *
     * @param row Row index.
     * @param col Column index.
     * @return Value at (row, col), or 0 if not present.
     * @throws IndexOutOfBoundsException if row or col is out of bounds.
     */
    public int getValue(int row, int col)
    {
        // Search non-zero values in row
        for (int idx = rowPtr[row]; idx < rowPtr[row + 1]; idx++)
        {
            if (colInd[idx] == col)
            {
                return values[idx];
            }
        }
        // Zero if not found
        return 0;
    }

    /**
     * Prints CSR matrix information with a title and optional size.
     *
     * @param title Title to print.
     * @param includeSize Whether to print the size.
     * @param color ANSI color code for output.
     */
    private void printCSRData(String title, boolean includeSize, String color)
    {
        final String ANSI_RESET = "\u001B[0m";

        System.out.println(color + title + ANSI_RESET);
        if (includeSize)
        {
            System.out.println(color + "\tSize: " + size + ANSI_RESET);
        }
        System.out.println(color + "\tRow Pointers: " + GraphModel.arrayToString(rowPtr) + ANSI_RESET);
        System.out.println(color + "\tColumn Indices: " + GraphModel.arrayToString(colInd) + ANSI_RESET);
        System.out.println(color + "\tValues: " + GraphModel.arrayToString(values) + ANSI_RESET);
    }

    /**
     * Prints the adjacency matrix in CSR format.
     */
    public void printAdjacency()
    {
        final String ANSI_BLUE = "\u001B[34m";
        printCSRData("\t\tADJACENCY MATRIX (CSR):", true, ANSI_BLUE);
    }

    /**
     * Prints the Laplacian matrix in CSR format and, for small matrices, as a dense matrix.
     */
    public void printLaplacian()
    {
        final String ANSI_PURPLE = "\u001B[35m";
        printCSRData("\t\tLAPLACIAN MATRIX (CSR):", false, ANSI_PURPLE);

        // Print dense matrix for small size
        if (size <= 100)
        {
            String ANSI_MAGENTA = "\u001B[95m";
            printDenseMatrix("\t\tLAPLACIAN MATRIX (DENSE):", ANSI_MAGENTA);
        }
    }

    /**
     * Prints the dense matrix representation of this CSR matrix.
     *
     * @param title Title to print.
     * @param color ANSI color code for output.
     */
    public void printDenseMatrix(String title, String color)
    {
        final String ANSI_RESET = "\u001B[0m";

        System.out.println(color + title + ANSI_RESET);

        // Find max width for alignment
        int maxWidth = 0;
        for (int row = 0; row < size; row++)
        {
            for (int col = 0; col < size; col++)
            {
                maxWidth = Math.max(maxWidth, String.valueOf(getValue(row, col)).length());
            }
        }

        for (int row = 0; row < size; row++)
        {
            System.out.print(color + "\t[");
            for (int col = 0; col < size; col++)
            {
                // Print value with spacing
                System.out.printf("%" + maxWidth + "d", getValue(row, col));
                if (col < size - 1) System.out.print(", ");
            }
            System.out.println("]" + ANSI_RESET);
        }
    }

    /**
     * Masks edges that are not cut by clusters, keeping only intra-cluster edges.
     * Returns a new CSRmatrix with only edges where both vertices are in the same cluster.
     * Also prints the resulting adjacency matrix.
     *
     * @param original The original CSRmatrix.
     * @param clusters Array of cluster indices for each vertex.
     * @return Masked CSRmatrix with only intra-cluster edges.
     */
    public static CSRmatrix maskCutEdges(CSRmatrix original, int[] clusters)
    {
        int size = original.size();
        int[] rowPtr = new int[size + 1];
        // Use dynamic lists to build new colInd and values
        java.util.List<Integer> newColInd = new java.util.ArrayList<>();
        java.util.List<Integer> newValues = new java.util.ArrayList<>();

        int nnz = 0;
        for (int row = 0; row < size; row++)
        {
            rowPtr[row] = nnz;
            for (int idx = original.rowPtr()[row]; idx < original.rowPtr()[row + 1]; idx++)
            {
                int col = original.colInd()[idx];
                int val = original.values()[idx];
                // Only keep edge if both vertices are in the same cluster
                if (clusters[row] == clusters[col])
                {
                    newColInd.add(col);
                    newValues.add(val);
                    nnz++;
                }
            }
        }
        rowPtr[size] = nnz;

        // Convert lists to arrays
        int[] colIndArr = newColInd.stream().mapToInt(i -> i).toArray();
        int[] valuesArr = newValues.stream().mapToInt(i -> i).toArray();

        CSRmatrix masked = new CSRmatrix(rowPtr, colIndArr, valuesArr, size);
        masked.printAdjacency();
        return masked;
    }

    // --- Getters for matrix data ---

    /**
     * Gets the row pointers array.
     * @return Row pointer array.
     */
    public int[] getRowPtr() { return rowPtr; }

    /**
     * Gets the column indices array.
     * @return Column indices array.
     */
    public int[] getColInd() { return colInd; }

    /**
     * Gets the values array.
     * @return Values array.
     */
    public int[] getValues() { return values; }

    /**
     * Gets the number of rows (and columns).
     * @return Matrix size.
     */
    public int getNumRows()
    {
        return size;
    }

    /**
     * Gets the adjacency (column indices) for a given row.
     *
     * @param i Row index.
     * @return Array of adjacent column indices for row i.
     * @throws IndexOutOfBoundsException if i is out of bounds.
     */
    public int[] getAdjacencyForRow(int i)
    {
        if (i < 0 || i >= size)
        {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + i);
        }
        int start = rowPtr[i];
        int end = rowPtr[i + 1];
        int[] adjacency = new int[end - start];
        System.arraycopy(colInd, start, adjacency, 0, end - start);
        return adjacency;
    }
}
