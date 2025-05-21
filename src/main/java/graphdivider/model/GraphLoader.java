package graphdivider.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class for loading a GraphModel from a file.
 * Intended to parse .csrrg files and construct GraphModel instances.
 */
public final class GraphLoader
{
    // Private constructor to prevent instantiation of utility class
    private GraphLoader() {}

    /**
     * Loads a GraphModel from a .csrrg file.
     * The file must have exactly 5 lines, each representing a different part of the graph structure.
     *
     * @param file the input .csrrg file
     * @return the loaded GraphModel instance
     * @throws IOException if file cannot be read or is malformed
     */
    public static GraphModel loadFromFile(File file) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String[] lines = new String[5];
            // Read exactly 5 lines from the file, each line is required
            for (int i = 0; i < 5; i++)
            {
                lines[i] = reader.readLine();
                if (lines[i] == null)
                {
                    throw new IOException("File has fewer than 5 lines: " + file.getName());
                }
            }
            // Parse each line into the appropriate data structure
            int maxVerticesPerRow = Integer.parseInt(lines[0].trim());
            int[] rowPositions = parseIntArray(lines[1]);
            int[] rowStartIndices = parseIntArray(lines[2]);
            int[] adjacencyList = parseIntArray(lines[3]);
            int[] adjacencyPointers = parseIntArray(lines[4]);

            // Print debug information about the loaded graph
            System.out.println("Loaded graph: maxVerticesPerRow=" + maxVerticesPerRow);
            System.out.println("Row positions: " + java.util.Arrays.toString(rowPositions));
            System.out.println("Row start indices: " + java.util.Arrays.toString(rowStartIndices));
            System.out.println("Adjacency list: " + java.util.Arrays.toString(adjacencyList));
            System.out.println("Adjacency pointers: " + java.util.Arrays.toString(adjacencyPointers));

            // Construct and return the GraphModel
            return new GraphModel(
                maxVerticesPerRow,
                rowPositions,
                rowStartIndices,
                adjacencyList,
                adjacencyPointers
            );
        }
    }

    /**
     * Converts a GraphModel to a CSRmatrix.
     * The adjacencyPointers array contains indices in adjacencyList where each vertex's adjacency starts.
     * The adjacencyList contains, for each vertex, the vertex index itself followed by its neighbors.
     *
     * @param model the GraphModel to convert
     * @return a CSRmatrix representing the same graph
     */
    public static CSRmatrix toCSRmatrix(GraphModel model)
    {
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();
        int size = adjacencyPointers.length;

        int[] rowPtr = new int[size + 1];
        // nnz: number of non-zero values (edges) in the matrix
        int nnz = 0;
        // Calculate row pointers and count neighbors for each vertex
        for (int i = 0; i < size; i++)
        {
            int start = adjacencyPointers[i];
            int end = (i + 1 < size) ? adjacencyPointers[i + 1] : adjacencyList.length;
            // The first value is the vertex index itself, skip it
            int neighbors = (end - start) - 1;
            rowPtr[i + 1] = rowPtr[i] + neighbors;
            nnz += neighbors;
        }

        int[] colInd = new int[nnz];
        int[] values = new int[nnz];

        int idx = 0;
        // Fill colInd and values arrays for CSR format
        for (int i = 0; i < size; i++)
        {
            int start = adjacencyPointers[i];
            int end = (i + 1 < size) ? adjacencyPointers[i + 1] : adjacencyList.length;
            // adjacencyList[start] is the vertex index itself, skip it
            for (int j = start + 1; j < end; j++)
            {
                colInd[idx] = adjacencyList[j];
                values[idx] = 1; // Assuming unweighted graph, use 1 for each edge
                idx++;
            }
        }

        // Print debug information about the CSR matrix
        System.out.println("Loaded graph as CSR matrix: " + size + " rows, " + nnz + " non-zero values");
        System.out.println("Row pointers: " + java.util.Arrays.toString(rowPtr));
        System.out.println("Column indices: " + java.util.Arrays.toString(colInd));
        System.out.println("Values: " + java.util.Arrays.toString(values));

        return new CSRmatrix(rowPtr, colInd, values, size);
    }

    /**
     * Parses a semicolon-separated string of integers into an int array.
     *
     * @param line the input string (e.g., "1;2;3")
     * @return an array of integers
     */
    private static int[] parseIntArray(String line)
    {
        String[] tokens = line.trim().split(";");
        int[] arr = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++)
        {
            arr[i] = Integer.parseInt(tokens[i]);
        }
        return arr;
    }
}

