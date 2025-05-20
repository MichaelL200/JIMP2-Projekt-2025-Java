package graphdivider.model;

public class CSRmatrix
{
    private final int[] rowPtr;
    private final int[] colInd;
    private final int[] values;
    private final int size;

    public CSRmatrix(int[] rowPtr, int[] colInd, int[] values, int size)
    {
        this.rowPtr = rowPtr;
        this.colInd = colInd;
        this.values = values;
        this.size = size;
    }

    public int getSize()
    {
        return size;
    }

    public int[] getRowPtr()
    {
        return rowPtr;
    }

    public int[] getColInd()
    {
        return colInd;
    }

    public int[] getValues()
    {
        return values;
    }

    /**
     * Returns the value at the specified row and column.
     * If the value is not explicitly stored, returns 0.0.
     */
    public int getValue(int row, int col)
    {
        for (int idx = rowPtr[row]; idx < rowPtr[row + 1]; idx++)
        {
            if (colInd[idx] == col)
            {
                return values[idx];
            }
        }
        return 0;
    }
}
