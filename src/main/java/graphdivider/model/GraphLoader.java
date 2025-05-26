package graphdivider.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for loading a GraphModel from a file.
 * Intended to parse .csrrg files and construct GraphModel instances.
 */
public final class GraphLoader
{
    private static final Logger LOGGER = Logger.getLogger(GraphLoader.class.getName());

    private GraphLoader() {}

    /**
     * Loads a GraphModel from a .csrrg file.
     * @param file the input .csrrg file
     * @return the loaded GraphModel instance
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
                    throw new IOException("File has fewer than 5 lines: " + file.getName());
            }
            int maxVerticesPerRow = Integer.parseInt(lines[0].trim());
            int[] rowPositions = parseIntArray(lines[1]);
            int[] rowStartIndices = parseIntArray(lines[2]);
            int[] adjacencyList = parseIntArray(lines[3]);
            int[] adjacencyPointers = parseIntArray(lines[4]);

            logGraphInfo(maxVerticesPerRow, rowPositions, rowStartIndices, adjacencyList, adjacencyPointers);

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
     * @param model the GraphModel to convert
     * @return a CSRmatrix representing the same graph
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

        logCSRInfo(size, nnz, rowPtr, colInd, values);

        return new CSRmatrix(rowPtr, colInd, values, size);
    }

    /**
     * Computes the Laplacian matrix (L = D - A) in CSR format for the given graph model.
     * @param model the GraphModel to convert
     * @return a CSRmatrix representing the Laplacian matrix
     */
    public static CSRmatrix toLaplacianCSRmatrix(GraphModel model)
    {
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();

        int maxVertex = Arrays.stream(adjacencyList).max().orElse(-1);
        int size = maxVertex + 1;

        Map<Integer, Set<Integer>> neighborsMap = new HashMap<>();
        for (int i = 0; i < adjacencyPointers.length; i++)
        {
            int start = adjacencyPointers[i];
            int end = (i + 1 < adjacencyPointers.length) ? adjacencyPointers[i + 1] : adjacencyList.length;
            int vertex = adjacencyList[start];
            neighborsMap.putIfAbsent(vertex, new HashSet<>());
            for (int j = start + 1; j < end; j++)
            {
                int neighbor = adjacencyList[j];
                if (neighbor != vertex)
                {
                    neighborsMap.get(vertex).add(neighbor);
                    neighborsMap.putIfAbsent(neighbor, new HashSet<>());
                    neighborsMap.get(neighbor).add(vertex);
                }
            }
        }

        int[] rowPtr = new int[size + 1];
        List<Integer> colIndList = new ArrayList<>();
        List<Integer> valuesList = new ArrayList<>();
        int idx = 0;

        for (int i = 0; i < size; i++)
        {
            rowPtr[i] = idx;
            Set<Integer> neighbors = neighborsMap.getOrDefault(i, Collections.emptySet());
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

        logLaplacianInfo(size, idx, rowPtr, colInd, values);

        return new CSRmatrix(rowPtr, colInd, values, size);
    }

    private static int[] parseIntArray(String line)
    {
        return Arrays.stream(line.trim().split(";"))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private static void logGraphInfo(int maxVerticesPerRow, int[] rowPositions, int[] rowStartIndices, int[] adjacencyList, int[] adjacencyPointers)
    {
        if (LOGGER.isLoggable(Level.FINE))
        {
            LOGGER.fine("Loaded graph: maxVerticesPerRow=" + maxVerticesPerRow);
            LOGGER.fine("Row positions: " + Arrays.toString(rowPositions));
            LOGGER.fine("Row start indices: " + Arrays.toString(rowStartIndices));
            LOGGER.fine("Adjacency list: " + Arrays.toString(adjacencyList));
            LOGGER.fine("Adjacency pointers: " + Arrays.toString(adjacencyPointers));
        }
    }

    private static void logCSRInfo(int size, int nnz, int[] rowPtr, int[] colInd, int[] values)
    {
        if (LOGGER.isLoggable(Level.FINE))
        {
            LOGGER.fine("Loaded graph as CSR matrix: " + size + " rows, " + nnz + " non-zero values");
            LOGGER.fine("Row pointers: " + Arrays.toString(rowPtr));
            LOGGER.fine("Column indices: " + Arrays.toString(colInd));
            LOGGER.fine("Values: " + Arrays.toString(values));
        }
    }

    private static void logLaplacianInfo(int size, int nnz, int[] rowPtr, int[] colInd, int[] values)
    {
        if (LOGGER.isLoggable(Level.FINE))
        {
            LOGGER.fine("Laplacian CSR matrix: " + size + "x" + size + ", " + nnz + " non-zero values");
            // Only print full arrays for small graphs
            if (size <= 100)
            {
                LOGGER.fine("Row pointers: " + Arrays.toString(rowPtr));
                LOGGER.fine("Column indices: " + Arrays.toString(colInd));
                LOGGER.fine("Values: " + Arrays.toString(values));
            } else
            {
                LOGGER.fine("Row pointers: [length=" + rowPtr.length + "]");
                LOGGER.fine("Column indices: [length=" + colInd.length + "]");
                LOGGER.fine("Values: [length=" + values.length + "]");
            }
        }
    }
}
