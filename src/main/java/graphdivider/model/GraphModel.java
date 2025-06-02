package graphdivider.model;

// Graph in memory using CSR format.
public final class GraphModel
{
    private final int maxVerticesPerRow;
    private final int[] rowPositions;
    private final int[] rowStartIndices;
    private final int[] adjacencyList;
    private final int[] adjacencyPointers;

    // Constructor
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
        // Defensive copy only if input is not already safe (if you control input, can skip clone)
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

    // Print graph data for debugging
    public void printGraphData()
    {
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_RESET = "\u001B[0m";
        System.out.println(ANSI_CYAN + "\tmaxVerticesPerRow: " + maxVerticesPerRow + ANSI_RESET);
        System.out.print(ANSI_CYAN + "\trowPositions: ");
        System.out.println(java.util.Arrays.toString(rowPositions) + ANSI_RESET);
        System.out.print(ANSI_CYAN + "\trowStartIndices: ");
        System.out.println(java.util.Arrays.toString(rowStartIndices) + ANSI_RESET);
        System.out.print(ANSI_CYAN + "\tadjacencyList: ");
        System.out.println(java.util.Arrays.toString(adjacencyList) + ANSI_RESET);
        System.out.print(ANSI_CYAN + "\tadjacencyPointers: ");
        System.out.println(java.util.Arrays.toString(adjacencyPointers) + ANSI_RESET);
    }
}
