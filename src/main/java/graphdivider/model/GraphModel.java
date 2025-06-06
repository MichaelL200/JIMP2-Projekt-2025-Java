package graphdivider.model;

// Graph in memory using CSR format
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

    // Print graph data (for debugging)
    public void printGraphData()
    {
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_RESET = "\u001B[0m";

        System.out.println(ANSI_CYAN + "\t\tGRAPH MODEL:" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\tMax Vertices Per Row: " + maxVerticesPerRow + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\tRow Positions: " + arrayToString(rowPositions) + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\tRow Start Indices: " + arrayToString(rowStartIndices) + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\tAdjacency List: " + arrayToString(adjacencyList) + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\tAdjacency Pointers: " + arrayToString(adjacencyPointers) + ANSI_RESET);
    }

    // Convert array to string
    public static String arrayToString(int[] str)
    {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < str.length; i++)
        {
            sb.append(str[i]);
            if (i < str.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

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
}
