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

    // Accessors for internal arrays
    public int[] getRowPtr() { return rowPtr; }
    public int[] getColInd() { return colInd; }
    public int[] getValues() { return values; }

    // Prints the CSR matrix data to the terminal for debugging.
    public void printMatrixData(String label)
    {
        final String ANSI_MAGENTA = "\u001B[35m";
        final String ANSI_RESET = "\u001B[0m";
        if (label != null && !label.isEmpty())
        {
            System.out.println(ANSI_MAGENTA + label + ANSI_RESET);
        }
        System.out.print(ANSI_MAGENTA + "\trowPtr: ");
        System.out.println(java.util.Arrays.toString(rowPtr) + ANSI_RESET);
        System.out.print(ANSI_MAGENTA + "\tcolInd: ");
        System.out.println(java.util.Arrays.toString(colInd) + ANSI_RESET);
        System.out.print(ANSI_MAGENTA + "\tvalues: ");
        System.out.println(java.util.Arrays.toString(values) + ANSI_RESET);
    }
}
