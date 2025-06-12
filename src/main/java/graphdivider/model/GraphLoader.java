package graphdivider.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Utility class for loading GraphModel from file and converting to CSRmatrix.
 * Supports reading from .csrrg files and conversion to Laplacian matrices.
 */
public final class GraphLoader
{
    // Logger for debug/info messages
    private static final Logger LOGGER = Logger.getLogger(GraphLoader.class.getName());

    // Prevent instantiation of utility class
    private GraphLoader() {}

    /**
     * Loads a GraphModel from a .csrrg file.
     * The file must have 5 lines: maxVerticesPerRow, rowPositions, rowStartIndices, adjacencyList, adjacencyPointers.
     *
     * @param file File to load from.
     * @return GraphModel object built from file data.
     * @throws IOException if file cannot be read or is malformed.
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
                    throw new IOException("File has fewer than 5 lines: " + file.getName());
            }
            int maxVerticesPerRow = Integer.parseInt(lines[0].trim());
            int[] rowPositions = parseIntArray(lines[1]);
            int[] rowStartIndices = parseIntArray(lines[2]);
            int[] adjacencyList = parseIntArray(lines[3]);
            int[] adjacencyPointers = parseIntArray(lines[4]);

            // Build and return model
            return new GraphModel
            (
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
     * 
     * @param model GraphModel to convert.
     * @return CSRmatrix representation of the graph.
     */
    public static CSRmatrix toCSRmatrix(GraphModel model)
    {
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();
        int size = adjacencyPointers.length;

        int[] rowPtr = new int[size + 1];
        int nnz = 0;
        for (int i = 0; i < size; i++)
        {
            int start = adjacencyPointers[i];
            int end = (i + 1 < size) ? adjacencyPointers[i + 1] : adjacencyList.length;
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
            for (int j = start + 1; j < end; j++)
            {
                colInd[idx] = adjacencyList[j];
                values[idx] = 1;
                idx++;
            }
        }

        return new CSRmatrix(rowPtr, colInd, values, size);
    }

    /**
     * Converts a GraphModel to a Laplacian CSRmatrix.
     * The Laplacian matrix is useful for spectral graph algorithms.
     *
     * @param model GraphModel to convert.
     * @return Laplacian CSRmatrix.
     */
    public static CSRmatrix toLaplacianCSRmatrix(GraphModel model)
    {
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();

        int maxVertex = Arrays.stream(adjacencyList).max().orElse(-1);
        int size = maxVertex + 1;

        // Build neighbor map for each vertex
        java.util.Map<Integer, java.util.Set<Integer>> neighborsMap = new java.util.HashMap<>();
        for (int i = 0; i < adjacencyPointers.length; i++)
        {
            int start = adjacencyPointers[i];
            int end = (i + 1 < adjacencyPointers.length) ? adjacencyPointers[i + 1] : adjacencyList.length;
            int vertex = adjacencyList[start];
            neighborsMap.putIfAbsent(vertex, new java.util.HashSet<>());
            for (int j = start + 1; j < end; j++)
            {
                int neighbor = adjacencyList[j];
                if (neighbor != vertex)
                {
                    neighborsMap.get(vertex).add(neighbor);
                    neighborsMap.putIfAbsent(neighbor, new java.util.HashSet<>());
                    neighborsMap.get(neighbor).add(vertex);
                }
            }
        }

        int[] rowPtr = new int[size + 1];
        java.util.List<Integer> colIndList = new java.util.ArrayList<>();
        java.util.List<Integer> valuesList = new java.util.ArrayList<>();
        int idx = 0;

        for (int i = 0; i < size; i++)
        {
            rowPtr[i] = idx;
            java.util.Set<Integer> neighbors = neighborsMap.getOrDefault(i, java.util.Collections.emptySet());
            colIndList.add(i);
            valuesList.add(neighbors.size());
            idx++;
            for (int neighbor : neighbors)
            {
                if (neighbor == i) continue;
                colIndList.add(neighbor);
                valuesList.add(-1);
                idx++;
            }
        }
        rowPtr[size] = idx;

        int[] colInd = colIndList.stream().mapToInt(Integer::intValue).toArray();
        int[] values = valuesList.stream().mapToInt(Integer::intValue).toArray();

        return new CSRmatrix(rowPtr, colInd, values, size);
    }

    /**
     * Parses an int array from a semicolon-separated string.
     *
     * @param line String containing semicolon-separated integers.
     * @return Parsed int array.
     */
    private static int[] parseIntArray(String line)
    {
        return Arrays.stream(line.trim().split(";")).mapToInt(Integer::parseInt).toArray();
    }
}