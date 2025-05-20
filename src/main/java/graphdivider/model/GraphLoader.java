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
    private GraphLoader() {}

    /**
     * Loads a GraphModel from a .csrrg file.
     * @param file the input file
     * @return the loaded GraphModel
     * @throws IOException if file cannot be read or is malformed
     */
    public static GraphModel loadFromFile(File file) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String[] lines = new String[5];
            for (int i = 0; i < 5; i++)
            {
                lines[i] = reader.readLine();
                if (lines[i] == null)
                {
                    throw new IOException("File has fewer than 5 lines: " + file.getName());
                }
            }
            int maxVerticesPerRow = Integer.parseInt(lines[0].trim());
            int[] rowPositions = parseIntArray(lines[1]);
            int[] rowStartIndices = parseIntArray(lines[2]);
            int[] adjacencyList = parseIntArray(lines[3]);
            int[] adjacencyPointers = parseIntArray(lines[4]);

            System.out.println("Loaded graph: maxVerticesPerRow=" + maxVerticesPerRow);
            System.out.println("Row positions: " + java.util.Arrays.toString(rowPositions));
            System.out.println("Row start indices: " + java.util.Arrays.toString(rowStartIndices));
            System.out.println("Adjacency list: " + java.util.Arrays.toString(adjacencyList));
            System.out.println("Adjacency pointers: " + java.util.Arrays.toString(adjacencyPointers));

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
     * The adjacencyList contains, for each vertex, the vertex index followed by its neighbors.
     */
    public static CSRmatrix toCSRmatrix(GraphModel model)
    {
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();
        int size = adjacencyPointers.length;

        int[] rowPtr = new int[size + 1];
        // First, count the number of neighbors for each vertex
        int nnz = 0;
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
        for (int i = 0; i < size; i++)
        {
            int start = adjacencyPointers[i];
            int end = (i + 1 < size) ? adjacencyPointers[i + 1] : adjacencyList.length;
            // adjacencyList[start] is the vertex index itself, skip it
            for (int j = start + 1; j < end; j++)
            {
                colInd[idx] = adjacencyList[j];
                values[idx] = 1; // Assuming unweighted graph, use 1.0 for each edge
                idx++;
            }
        }

        System.out.println("Loaded graph as CSR matrix: " + size + " rows, " + nnz + " non-zero values");
        System.out.println("Row pointers: " + java.util.Arrays.toString(rowPtr));
        System.out.println("Column indices: " + java.util.Arrays.toString(colInd));
        System.out.println("Values: " + java.util.Arrays.toString(values));

        return new CSRmatrix(rowPtr, colInd, values, size);
    }

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
