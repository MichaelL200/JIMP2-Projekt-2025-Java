package graphdivider.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
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
     * Loads a GraphModel and its matrices from a file.
     * @param file the input .csrrg file
     * @return LoadedGraph containing the model, CSR matrix, and Laplacian
     * @throws IOException if file cannot be read or is malformed
     */
    public static LoadedGraph loadGraphWithMatrices(File file) throws IOException
    {
        GraphModel model = loadFromFile(file);
        model.printGraphData();
        CSRmatrix matrix = toCSRmatrix(model);
        // Delegate Laplacian calculation to GraphPartitioner in model package
        CSRmatrix laplacian = GraphPartitioner.toLaplacianCSRmatrix(model);
        laplacian.printMatrixData("Laplacian CSR matrix data:");
        return new LoadedGraph(model, matrix, laplacian);
    }

    /**
     * Data holder for loaded graph model, matrix, and Laplacian matrix.
     */
    public static class LoadedGraph
    {
        public final GraphModel model;
        public final CSRmatrix matrix;
        public final CSRmatrix laplacian;

        public LoadedGraph(GraphModel model, CSRmatrix matrix, CSRmatrix laplacian)
        {
            this.model = model;
            this.matrix = matrix;
            this.laplacian = laplacian;
        }
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
}
