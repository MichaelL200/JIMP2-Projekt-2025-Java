package graphdivider.io;

import graphdivider.model.CSRmatrix;
import graphdivider.model.GraphModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// Utility class for writing graph partition assignments to a text file.
public class Output
{

    /**
     * Writes vertex-to-part assignments to a text file in the .csrrg2 format:
     * <number_of_parts> <cut_edges> <margin_kept>
     * <maxVerticesPerRow>
     * <rowPositions>
     * <rowStartIndices>
     */
    public static void writeText(File file, int numParts, int edgesCut, double marginKept, GraphModel graphModel, CSRmatrix adjacencyDivided) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            // Result
            writer.write(numParts + " " + edgesCut + " " + String.format("%.2f", marginKept));
            writer.newLine();

            writer.write(String.valueOf(graphModel.getMaxVerticesPerRow()));
            writer.newLine();

            writer.write(arrayToSemicolonString(graphModel.getRowPositions()));
            writer.newLine();

            writer.write(arrayToSemicolonString(graphModel.getRowStartIndices()));
            writer.newLine();

            int n = adjacencyDivided.getNumRows();
            int pointer = 0;
            int pointers_count = 0;
            int[] pointers = new int[n];
            for (int i = 0; i < n; i++)
            {
                int[] adj = adjacencyDivided.getAdjacencyForRow(i);
                writer.write(i + ";");
                pointers[pointers_count++] = pointer++;
                for (int v : adj)
                {
                    writer.write(v + ";");
                    pointer++;
                }
            }


            writer.newLine();

            // Write pointers
            for (int i = 0; i < pointers_count; i++)
            {
                writer.write(pointers[i] + (i < pointers_count - 1 ? ";" : ""));
            }
        }
    }

    // Convert int array to semicolon-separated string
    private static String arrayToSemicolonString(int[] arr)
    {
        if (arr == null || arr.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++)
        {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(";");
        }
        return sb.toString();
    }
}
