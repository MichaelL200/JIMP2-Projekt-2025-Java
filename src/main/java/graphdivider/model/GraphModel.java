package graphdivider.model;

/**
 * Represents a graph in memory using CSR (Compressed Sparse Row) format.
 * Stores all data needed for efficient graph operations and visualization.
 */
public final class GraphModel
{
    // Maximum number of vertices in any row
    private final int maxVerticesPerRow;
    // Row positions for each vertex (layout)
    private final int[] rowPositions;
    // Start index of each row in adjacency list
    private final int[] rowStartIndices;
    // Adjacency list (CSR format)
    private final int[] adjacencyList;
    // Pointers to adjacency list for each vertex
    private final int[] adjacencyPointers;

    /**
     * Creates a graph model from the provided arrays.
     * Arrays are assumed to be safe to use directly (not copied).
     *
     * @param maxVerticesPerRow Maximum number of vertices in any row.
     * @param rowPositions Array of row positions for each vertex.
     * @param rowStartIndices Array of start indices for each row in adjacency list.
     * @param adjacencyList Adjacency list in CSR format.
     * @param adjacencyPointers Pointers to adjacency list for each vertex.
     */
    public GraphModel
    (
        int maxVerticesPerRow,
        int[] rowPositions,
        int[] rowStartIndices,
        int[] adjacencyList,
        int[] adjacencyPointers
    )
    {
        this.maxVerticesPerRow  = maxVerticesPerRow;
        // Arrays are assumed safe to use directly
        this.rowPositions = rowPositions;
        this.rowStartIndices = rowStartIndices;
        this.adjacencyList = adjacencyList;
        this.adjacencyPointers = adjacencyPointers;
    }

    /**
     * Gets the maximum number of vertices in any row.
     *
     * @return Maximum vertices per row.
     */
    public int getMaxVerticesPerRow() 
    { 
        return maxVerticesPerRow; 
    }

    /**
     * Gets the row positions array.
     *
     * @return Array of row positions for each vertex.
     */
    public int[] getRowPositions() 
    { 
        return rowPositions; 
    }

    /**
     * Gets the row start indices array.
     *
     * @return Array of start indices for each row in adjacency list.
     */
    public int[] getRowStartIndices() 
    { 
        return rowStartIndices; 
    }

    /**
     * Gets the adjacency list array (CSR format).
     *
     * @return Adjacency list array.
     */
    public int[] getAdjacencyList() 
    { 
        return adjacencyList; 
    }

    /**
     * Gets the adjacency pointers array.
     *
     * @return Array of pointers to adjacency list for each vertex.
     */
    public int[] getAdjacencyPointers() 
    { 
        return adjacencyPointers; 
    }

    /**
     * Gets the CSRmatrix representation of the graph.
     *
     * @return CSRmatrix object representing the graph.
     */
    public CSRmatrix getCSRmatrix()
    {
        return new CSRmatrix
        (
            rowStartIndices,
            adjacencyList,
            adjacencyPointers,
            rowPositions.length
        );
    }

    /**
     * Utility: Converts an int array to a string for debugging or printing.
     *
     * @param arr Array to convert.
     * @return String representation of the array.
     */
    public static String arrayToString(int[] arr)
    {
        if (arr == null) return "null";
        if (arr.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++)
        {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append("; ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Prints the graph data to the console for debugging.
     * Uses ANSI color codes for better readability.
     */
    public void printGraphData()
    {
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_RESET = "\u001B[0m";
        System.out.println(ANSI_RESET + "\t\tGRAPH DATA" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\tmaxVerticesPerRow: " + maxVerticesPerRow + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\trowPositions: " + arrayToString(rowPositions) + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\trowStartIndices: " + arrayToString(rowStartIndices) + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\tadjacencyList: " + arrayToString(adjacencyList) + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\tadjacencyPointers: " + arrayToString(adjacencyPointers) + ANSI_RESET);
    }
}
