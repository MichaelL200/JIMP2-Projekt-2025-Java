package graphdivider.model;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

// Loads GraphModel from file and converts to CSRmatrix
public final class GraphLoader
{
    private static final Logger LOGGER = Logger.getLogger(GraphLoader.class.getName());

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
        CSRmatrix laplacian = GraphEigenvalues.toLaplacianCSRmatrix(model);

        return laplacian;
    }

    // Parse int array from semicolon-separated string
    private static int[] parseIntArray(String line)
    {
        return Arrays.stream(line.trim().split(";"))
                .mapToInt(Integer::parseInt)
                .toArray();
    }
}
