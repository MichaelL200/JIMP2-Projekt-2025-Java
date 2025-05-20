package graphdivider.model;

/**
 * Represents a graph in memory using the Compressed Sparse Row (CSR) format.
 *
 * The associated .csrrg file must contain exactly five lines, each line consisting of integer values separated by semicolons:
 *
 * 1. maxVerticesPerRow        - Maximum number of vertices in any row (single integer).
 * 2. rowPositions             - Array of row identifiers.
 * 3. rowStartIndices          - For each row, the index in adjacencyList where that row’s neighbors start.
 * 4. adjacencyList            - Flattened array of all neighbor vertex indices.
 * 5. adjacencyPointers        - For each vertex (in row order), the index in adjacencyList where its neighbor list begins.
 */
public final class GraphModel
{
    /** Maximum number of vertices present in any row. */
    private final int maxVerticesPerRow;

    /** Identifiers for each row in the graph. */
    private final int[] rowPositions;

    /** For each row, the starting index in adjacencyList for its neighbors. */
    private final int[] rowStartIndices;

    /** Flattened list of all neighbor vertex indices for the graph. */
    private final int[] adjacencyList;

    /**
     * For each vertex (ordered by row), the index in adjacencyList where its neighbor list begins.
     */
    private final int[] adjacencyPointers;

    GraphModel
    (
        int maxVerticesPerRow,
        int[] rowPositions,
        int[] rowStartIndices,
        int[] adjacencyList,
        int[] adjacencyPointers)
    {
        this.maxVerticesPerRow  = maxVerticesPerRow;
        this.rowPositions       = rowPositions;
        this.rowStartIndices    = rowStartIndices;
        this.adjacencyList      = adjacencyList;
        this.adjacencyPointers  = adjacencyPointers;
    }

    // Getters

    public int getMaxVerticesPerRow()
    {
        return maxVerticesPerRow;
    }

    public int[] getRowPositions()
    {
        return rowPositions;
    }

    public int[] getRowStartIndices()
    {
        return rowStartIndices;
    }

    public int[] getAdjacencyList()
    {
        return adjacencyList;
    }

    public int[] getAdjacencyPointers()
    {
        return adjacencyPointers;
    }
}
