package graphdivider.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

// Loads GraphModel from file and converts to CSRmatrix
public final class GraphLoader
{
    // Logger for debug/info
    private static final Logger LOGGER = Logger.getLogger(GraphLoader.class.getName());

    // Prevent instantiation
    private GraphLoader() {}

    // Load GraphModel from .csrrg file
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

    // Convert GraphModel to CSRmatrix
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

    // Convert GraphModel to Laplacian CSRmatrix
    public static CSRmatrix toLaplacianCSRmatrix(GraphModel model)
    {
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();

        int maxVertex = Arrays.stream(adjacencyList).max().orElse(-1);
        int size = maxVertex + 1;

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

    // Parse int array from semicolon-separated string
    private static int[] parseIntArray(String line)
    {
        return Arrays.stream(line.trim().split(";")).mapToInt(Integer::parseInt).toArray();
    }
}