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
        System.out.println("size: " + size);
        System.out.print("rowPtr: ");
        System.out.println(java.util.Arrays.toString(rowPtr));
        System.out.print("colInd: ");
        System.out.println(java.util.Arrays.toString(colInd));
        System.out.print("values: ");
        System.out.println(java.util.Arrays.toString(values));
        if (size <= 10)
        {
            System.out.println("Full matrix:");
            // Print column headers
            System.out.print("     ");
            for (int j = 0; j < size; j++)
            {
                System.out.printf("%4d", j);
            }
            System.out.println();
            System.out.print("    ");
            for (int j = 0; j < size; j++)
            {
                System.out.print("----");
            }
            System.out.println();
            // Print each row with row index
            for (int i = 0; i < size; i++)
            {
                System.out.printf("%3d|", i);
                for (int j = 0; j < size; j++)
                {
                    System.out.printf("%4d", getValue(i, j));
                }
                System.out.println();
            }
        }
    }
}
