package graphdivider.model;

// Sparse matrix in Compressed Sparse Row (CSR) format
public record CSRmatrix(int[] rowPtr, int[] colInd, int[] values, int size)
{
    // Get value at (row, col)
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

    // Print CSR matrix info (helper)
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

    // Print adjacency matrix (CSR)
    public void printAdjacency()
    {
        final String ANSI_BLUE = "\u001B[34m";
        printCSRData("\t\tADJACENCY MATRIX (CSR):", true, ANSI_BLUE);
    }

    // Print Laplacian matrix (CSR and optionally dense)
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

    // Print dense matrix from CSR
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

    // Mask edges that are not cut by clusters
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

    // Get row pointers
    public int[] getRowPtr() { return rowPtr; }
    // Get column indices
    public int[] getColInd() { return colInd; }
    // Get values
    public int[] getValues() { return values; }
}
