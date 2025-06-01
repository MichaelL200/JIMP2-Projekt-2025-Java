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
    public GraphModel(
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
        System.out.println("GraphModel data:");
        System.out.println("maxVerticesPerRow: " + maxVerticesPerRow);
        System.out.print("rowPositions: ");
        System.out.println(java.util.Arrays.toString(rowPositions));
        System.out.print("rowStartIndices: ");
        System.out.println(java.util.Arrays.toString(rowStartIndices));
        System.out.print("adjacencyList: ");
        System.out.println(java.util.Arrays.toString(adjacencyList));
        System.out.print("adjacencyPointers: ");
        System.out.println(java.util.Arrays.toString(adjacencyPointers));
    }
}
