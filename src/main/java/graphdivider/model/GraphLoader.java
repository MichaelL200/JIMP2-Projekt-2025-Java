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

            // Print adjacency info for each vertex for debugging
            for (int i = 0; i < adjacencyPointers.length; i++)
            {
                int start = adjacencyPointers[i];
                int end = (i + 1 < adjacencyPointers.length) ? adjacencyPointers[i + 1] : adjacencyList.length;
                int vertexIdx = adjacencyList[start];
                System.out.print("Vertex " + vertexIdx + " neighbors: ");
                for (int j = start + 1; j < end; j++)
                {
                    System.out.print(adjacencyList[j] + " ");
                }
                System.out.println();
            }

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
     * Computes the Laplacian matrix (L = D - A) in CSR format for the given graph model.
     * The Laplacian is defined as:
     *   L[i][j] = degree(i) if i == j
     *           = -1        if i != j and (i, j) is an edge
     *           = 0         otherwise
     *
     * @param model the GraphModel to convert
     * @return a CSRmatrix representing the Laplacian matrix
     */
    public static CSRmatrix toLaplacianCSRmatrix(GraphModel model)
    {
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();

        // Find the maximum vertex index to determine the matrix size
        int maxVertex = -1;
        for (int v : adjacencyList) {
            if (v > maxVertex) maxVertex = v;
        }
        int size = maxVertex + 1;

        // Build undirected neighbor sets for all vertices
        java.util.Map<Integer, java.util.Set<Integer>> neighborsMap = new java.util.HashMap<>();
        for (int i = 0; i < adjacencyPointers.length; i++) {
            int start = adjacencyPointers[i];
            int end = (i + 1 < adjacencyPointers.length) ? adjacencyPointers[i + 1] : adjacencyList.length;
            int vertex = adjacencyList[start];
            neighborsMap.putIfAbsent(vertex, new java.util.HashSet<>());
            for (int j = start + 1; j < end; j++) {
                int neighbor = adjacencyList[j];
                if (neighbor != vertex) {
                    neighborsMap.get(vertex).add(neighbor);
                    // Ensure undirected: add vertex to neighbor's set as well
                    neighborsMap.putIfAbsent(neighbor, new java.util.HashSet<>());
                    neighborsMap.get(neighbor).add(vertex);
                }
            }
        }

        // Prepare CSR arrays for all vertices 0..size-1
        int[] rowPtr = new int[size + 1];
        java.util.List<Integer> colIndList = new java.util.ArrayList<>();
        java.util.List<Integer> valuesList = new java.util.ArrayList<>();
        int idx = 0;

        for (int i = 0; i < size; i++) {
            rowPtr[i] = idx;
            java.util.Set<Integer> neighbors = neighborsMap.getOrDefault(i, java.util.Collections.emptySet());
            // Diagonal: degree (number of unique undirected neighbors)
            colIndList.add(i);
            valuesList.add(neighbors.size());
            idx++;
            // Off-diagonal: -1 for each unique neighbor
            for (int neighbor : neighbors) {
                if (neighbor == i) continue;
                colIndList.add(neighbor);
                valuesList.add(-1);
                idx++;
            }
        }
        rowPtr[size] = idx;

        int[] colInd = new int[idx];
        int[] values = new int[idx];
        for (int i = 0; i < idx; i++) {
            colInd[i] = colIndList.get(i);
            values[i] = valuesList.get(i);
        }

        // Debug print
        System.out.println("Laplacian CSR matrix: " + size + "x" + size + ", " + idx + " non-zero values");
        System.out.println("Row pointers: " + java.util.Arrays.toString(rowPtr));
        System.out.println("Column indices: " + java.util.Arrays.toString(colInd));
        System.out.println("Values: " + java.util.Arrays.toString(values));

        CSRmatrix laplacian = new CSRmatrix(rowPtr, colInd, values, size);

        // Print the full Laplacian matrix
        System.out.println("Full Laplacian matrix:");
        for (int i = 0; i < size; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < size; j++) {
                sb.append(laplacian.getValue(i, j));
                if (j < size - 1) sb.append(" ");
            }
            System.out.println(sb);
        }

        return laplacian;
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
