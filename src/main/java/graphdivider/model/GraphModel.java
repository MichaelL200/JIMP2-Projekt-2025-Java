package graphdivider.model;

// Represents a graph in memory using CSR format.
public final class GraphModel
{
    // Max number of vertices in any row
    private final int maxVerticesPerRow;
    // Row positions for each vertex
    private final int[] rowPositions;
    // Start index of each row in adjacency list
    private final int[] rowStartIndices;
    // Adjacency list (CSR)
    private final int[] adjacencyList;
    // Pointers to adjacency list for each vertex
    private final int[] adjacencyPointers;

    // Create graph model from arrays
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

    // Get max vertices per row
    public int getMaxVerticesPerRow() { return maxVerticesPerRow; }

    // Get row positions
    public int[] getRowPositions() { return rowPositions; }

    // Get row start indices
    public int[] getRowStartIndices() { return rowStartIndices; }

    // Get adjacency list
    public int[] getAdjacencyList() { return adjacencyList; }

    // Get adjacency pointers
    public int[] getAdjacencyPointers() { return adjacencyPointers; }

    // Get CSRmatrix representation of the graph
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

    // Utility: Convert int array to string (for debug/printing)
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

    // Print graph data for debugging
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
