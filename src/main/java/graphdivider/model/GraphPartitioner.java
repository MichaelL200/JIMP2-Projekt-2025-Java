package graphdivider.model;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Utility class for spectral partitioning of graphs.
public final class GraphPartitioner
{
    private static final Logger LOGGER = Logger.getLogger(GraphPartitioner.class.getName());

    private GraphPartitioner() {}

    // Computes the Laplacian matrix (L = D - A) in CSR format for the given graph model.
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

    // Logs Laplacian matrix info for debugging.
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
