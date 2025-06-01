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

    // Prints the CSR matrix data to the terminal for debugging.
    public void printMatrixData(String label)
    {
        if (label != null && !label.isEmpty())
        {
            System.out.println(label);
        }
    }
}
