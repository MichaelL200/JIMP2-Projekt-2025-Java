package graphdivider.model;

// Represents a graph in Compressed Sparse Row (CSR) format
public final class GraphModel
{
    // The maximum number of vertices present in any row of the graph
    private final int maxVerticesPerRow;

    // Array containing the identifiers for each row in the graph
    private final int[] rowPositions;

    // For each row, the starting index in adjacencyList for its neighbors
    private final int[] rowStartIndices;

    // Flattened list of all neighbor vertex indices for the graph.
    private final int[] adjacencyList;

    // For each vertex (ordered by row), the index in adjacencyList where its neighbor list begins
    private final int[] adjacencyPointers;

    // Constructs a GraphModel with all required CSR arrays
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
        // Defensive copies to preserve immutability
        this.rowPositions = rowPositions.clone();
        this.rowStartIndices = rowStartIndices.clone();
        this.adjacencyList = adjacencyList.clone();
        this.adjacencyPointers = adjacencyPointers.clone();
    }

    // GETTERS

    // Returns the maximum number of vertices present in any row of the graph
    public int getMaxVerticesPerRow()
    {
        return maxVerticesPerRow;
    }

    // Returns array containing the identifiers for each row in the graph
    public int[] getRowPositions()
    {
        return rowPositions;
    }

    // Returns for each row, the starting index in adjacencyList for its neighbors
    public int[] getRowStartIndices()
    {
        return rowStartIndices;
    }

    // Returns flattened list of all neighbor vertex indices for the graph
    public int[] getAdjacencyList()
    {
        return adjacencyList;
    }

    // Returns for each vertex (ordered by row)
    public int[] getAdjacencyPointers()
    {
        return adjacencyPointers;
    }

    // Prints the graph's data arrays to the terminal for debugging purposes
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