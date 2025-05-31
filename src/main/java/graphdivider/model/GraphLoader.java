package graphdivider.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

// Utility class for loading a GraphModel from a file
public final class GraphLoader
{
    // Logger for debug output
    private static final Logger LOGGER = Logger.getLogger(GraphLoader.class.getName());

    // Private constructor to prevent instantiation
    private GraphLoader() {}

    // Loads a GraphModel from a .csrrg file
    public static GraphModel loadFromFile(File file) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            // Read the first 5 lines from the file
            String[] lines = new String[5];
            for (int i = 0; i < 5; i++)
            {
                lines[i] = reader.readLine();
                if (lines[i] == null)
                    throw new IOException("File has fewer than 5 lines: " + file.getName());
            }
            // Parse each line into the appropriate array or value
            int maxVerticesPerRow = Integer.parseInt(lines[0].trim());
            int[] rowPositions = parseIntArray(lines[1]);
            int[] rowStartIndices = parseIntArray(lines[2]);
            int[] adjacencyList = parseIntArray(lines[3]);
            int[] adjacencyPointers = parseIntArray(lines[4]);

            // Log the loaded graph info
            logGraphInfo(maxVerticesPerRow, rowPositions, rowStartIndices, adjacencyList, adjacencyPointers);

            // Return the constructed GraphModel
            return new GraphModel(
                    maxVerticesPerRow,
                    rowPositions,
                    rowStartIndices,
                    adjacencyList,
                    adjacencyPointers
            );
        }
    }

    // Converts a GraphModel to a CSRmatrix
    public static CSRmatrix toCSRmatrix(GraphModel model)
    {
        // Get adjacency data from the model
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();
        int size = adjacencyPointers.length;

        // Prepare row pointer and count non-zeros
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

        // Prepare column indices and values arrays
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

        // Log the CSR matrix info
        logCSRInfo(size, nnz, rowPtr, colInd, values);

        // Return the CSRmatrix
        return new CSRmatrix(rowPtr, colInd, values, size);
    }

    // Loads a GraphModel and its matrices from a file
    public static LoadedGraph loadGraphWithMatrices(File file) throws IOException
    {
        // Load the graph model from file
        GraphModel model = loadFromFile(file);
        model.printGraphData();
        // Convert to CSR matrix
        CSRmatrix matrix = toCSRmatrix(model);
        // Compute Laplacian matrix
        CSRmatrix laplacian = GraphPartitioner.toLaplacianCSRmatrix(model);
        laplacian.printMatrixData("Laplacian CSR matrix data:");
        // Return all loaded data
        return new LoadedGraph(model, matrix, laplacian);
    }

    // Data holder for loaded graph model, matrix, and Laplacian matrix
    public static class LoadedGraph
    {
        public final GraphModel model;
        public final CSRmatrix matrix;
        public final CSRmatrix laplacian;

        // Constructor for LoadedGraph
        public LoadedGraph(GraphModel model, CSRmatrix matrix, CSRmatrix laplacian)
        {
            this.model = model;
            this.matrix = matrix;
            this.laplacian = laplacian;
        }
    }

    // Parses a semicolon-separated string into an int array
    private static int[] parseIntArray(String line)
    {
        return Arrays.stream(line.trim().split(";"))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    // Logs information about the loaded graph
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

    // Logs information about the CSR matrix
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
}