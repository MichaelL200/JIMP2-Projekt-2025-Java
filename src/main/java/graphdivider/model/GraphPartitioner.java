package graphdivider.model;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class GraphPartitioner {

    private GraphPartitioner() {}

    /**
     * Loads a partitioned text graph file (.csrrg) and returns the loaded graph.
     * @param file the input .csrrg file
     * @return LoadedGraph containing the model, CSR matrix, and Laplacian
     * @throws IOException if file cannot be read or is malformed
     */
    public static GraphLoader.LoadedGraph loadPartitionedTextGraph(File file) throws IOException
    {
        // For now, reuse the same logic as GraphLoader for .csrrg files
        return GraphLoader.loadGraphWithMatrices(file);
    }

    /**
     * Loads a partitioned binary graph file (.bin) and returns the loaded graph.
     * @param file the input .bin file
     * @return LoadedGraph containing the model, CSR matrix, and Laplacian
     * @throws IOException if file cannot be read or is malformed
     */
    public static GraphLoader.LoadedGraph loadPartitionedBinaryGraph(File file) throws IOException
    {
        // Not implemented: throw for now
        throw new UnsupportedOperationException("Binary partitioned graph loading not implemented yet.");
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

        // Optionally: log or print Laplacian info here if needed

        return new CSRmatrix(rowPtr, colInd, values, size);
    }
}
