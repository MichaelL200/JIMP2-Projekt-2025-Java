package graphdivider.model;

// Represents a sparse matrix in Compressed Sparse Row (CSR) format.
public record CSRmatrix(int[] rowPtr, int[] colInd, int[] values, int size)
{
    // Returns the value at the specified row and column.
    public int getValue(int row, int col)
    {
        // Iterate over the non-zero values in the given row
        for (int idx = rowPtr[row]; idx < rowPtr[row + 1]; idx++)
        {
            if (colInd[idx] == col)
            {
                return values[idx];
            }
        }
        // If not found, the value is zero in the sparse representation
        return 0;
    }

    // Prints the CSR matrix data with tabs and colored output (helper method)
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

    // Prints the adjacency matrix data
    public void printAdjacency()
    {
        final String ANSI_BLUE = "\u001B[34m"; // Blue color
        printCSRData("\t\tADJACENCY MATRIX (CSR):", true, ANSI_BLUE);
    }

    // Prints the Laplacian matrix data
    public void printLaplacian()
    {
        final String ANSI_PURPLE = "\u001B[35m"; // Purple color
        printCSRData("\t\tLAPLACIAN MATRIX (CSR):", false, ANSI_PURPLE);

        // Dense matrix - only for small matrices
        if (size <= 100)
        {
            String ANSI_MAGENTA = "\u001B[95m"; // bright Magenta color
            printDenseMatrix("\t\tLAPLACIAN MATRIX (DENSE):", ANSI_MAGENTA);
        }
    }

    // Prints the dense matrix directly from the CSR format
    public void printDenseMatrix(String title, String color)
    {
        final String ANSI_RESET = "\u001B[0m";

        System.out.println(color + title + ANSI_RESET);

        // Determine the maximum width of any value for alignment
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
                // Format each value with consistent spacing
                System.out.printf("%" + maxWidth + "d", getValue(row, col));
                if (col < size - 1) System.out.print(", ");
            }
            System.out.println("]" + ANSI_RESET);
        }
    }

    // Accessors for internal arrays
    public int[] getRowPtr() { return rowPtr; }
    public int[] getColInd() { return colInd; }
    public int[] getValues() { return values; }
}
