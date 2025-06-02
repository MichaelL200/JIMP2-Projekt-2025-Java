package graphdivider.model;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class GraphPartitioner
{

    private GraphPartitioner() {}

    // Loads a partitioned text graph file (.csrrg) and returns the loaded graph
    public static GraphLoader.LoadedGraph loadPartitionedTextGraph(File file) throws IOException
    {
        // For now, reuse the same logic as GraphLoader for .csrrg files
        return GraphLoader.loadGraphWithMatrices(file);
    }

    // Loads a partitioned binary graph file (.bin) and returns the loaded graph
    public static GraphLoader.LoadedGraph loadPartitionedBinaryGraph(File file) throws IOException
    {
        // Not implemented: throw for now
        throw new UnsupportedOperationException("Binary partitioned graph loading not implemented yet.");
    }

    // Computes the Laplacian matrix (L = D - A) in CSR format for the given graph model
    public static CSRmatrix toLaplacianCSRmatrix(GraphModel model)
    {
        // Get adjacency list and pointers from the model
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();

        // Find the maximum vertex index to determine matrix size
        int maxVertex = Arrays.stream(adjacencyList).max().orElse(-1);
        int size = maxVertex + 1;

        // Build a map from each vertex to its set of neighbors
        Map<Integer, Set<Integer>> neighborsMap = new HashMap<>(size);
        for (int i = 0; i < adjacencyPointers.length; i++)
        {
            int start = adjacencyPointers[i];
            int end = (i + 1 < adjacencyPointers.length) ? adjacencyPointers[i + 1] : adjacencyList.length;
            int vertex = adjacencyList[start];
            neighborsMap.computeIfAbsent(vertex, k -> new HashSet<>());
            for (int j = start + 1; j < end; j++)
            {
                int neighbor = adjacencyList[j];
                if (neighbor != vertex)
                {
                    neighborsMap.get(vertex).add(neighbor);
                    neighborsMap.computeIfAbsent(neighbor, k -> new HashSet<>()).add(vertex);
                }
            }
        }

        // Prepare CSR matrix data structures
        int[] rowPtr = new int[size + 1];
        List<Integer> colIndList = new ArrayList<>(size * 2);
        List<Integer> valuesList = new ArrayList<>(size * 2);
        int idx = 0;

        // Construct each row of the Laplacian matrix
        for (int i = 0; i < size; i++)
        {
            rowPtr[i] = idx;
            Set<Integer> neighbors = neighborsMap.getOrDefault(i, Collections.emptySet());

            // Diagonal entry: degree of the vertex
            colIndList.add(i);
            valuesList.add(neighbors.size());
            idx++;

            // Off-diagonal entries: -1 for each neighbor
            for (int neighbor : neighbors)
            {
                if (neighbor == i) continue; // Skip self-loops
                colIndList.add(neighbor);
                valuesList.add(-1);
                idx++;
            }
        }
        rowPtr[size] = idx; // Set the last pointer

        // Convert lists to arrays for CSRmatrix
        int[] colInd = colIndList.stream().mapToInt(Integer::intValue).toArray();
        int[] values = valuesList.stream().mapToInt(Integer::intValue).toArray();

        // Return the Laplacian in CSR format
        return new CSRmatrix(rowPtr, colInd, values, size);
    }
}

