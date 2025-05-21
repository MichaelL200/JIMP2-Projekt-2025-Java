package graphdivider.model;

/**
 * Represents a sparse matrix in Compressed Sparse Row (CSR) format.
 * <p>
 * Fields:
 * - rowPtr:   Array of length (size + 1), where rowPtr[i] is the index in colInd/values where row i starts.
 * - colInd:   Column indices for each non-zero value.
 * - values:   Non-zero values of the matrix.
 * - size:     Number of rows (and columns, if square).
 */
public record CSRmatrix(int[] rowPtr, int[] colInd, int[] values, int size)
{
    /**
     * Returns the value at the specified row and column.
     * If the value is not explicitly stored (i.e., is zero in the sparse matrix), returns 0.
     *
     * @param row Row index (0-based)
     * @param col Column index (0-based)
     * @return Value at (row, col), or 0 if not present
     */
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
}
