package graphdivider.io;

import graphdivider.model.CSRmatrix;
import graphdivider.model.GraphModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

// Utility class for writing graph partition assignments to a text file.
public class Output
{
    // Saves the partitioned graph data in a text format (.txt)
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

    // Saves the partitioned graph data in a binary format (.bin)
    public static void writeBinary(File file, int numParts, int edgesCut, double marginKept, GraphModel graphModel, CSRmatrix adjacencyDivided) throws IOException
    {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file)))
        {
            // Write header
            out.writeInt(numParts);
            out.writeInt(edgesCut);
            out.writeDouble(marginKept);

            // Write maxVerticesPerRow
            out.writeInt(graphModel.getMaxVerticesPerRow());

            // Write rowPositions
            int[] rowPositions = graphModel.getRowPositions();
            out.writeInt(rowPositions.length);
            for (int v : rowPositions) out.writeInt(v);

            // Write rowStartIndices
            int[] rowStartIndices = graphModel.getRowStartIndices();
            out.writeInt(rowStartIndices.length);
            for (int v : rowStartIndices) out.writeInt(v);

            // Write adjacency data
            int n = adjacencyDivided.getNumRows();
            int pointer = 0;
            int pointers_count = 0;
            int[] pointers = new int[n];

            // Count total adjacency elements
            int totalAdjacencyElements = 0;
            for (int i = 0; i < n; i++) {
                totalAdjacencyElements += 1 + adjacencyDivided.getAdjacencyForRow(i).length;
            }
            out.writeInt(totalAdjacencyElements); // Write number of adjacency elements

            for (int i = 0; i < n; i++)
            {
                int[] adj = adjacencyDivided.getAdjacencyForRow(i);
                out.writeInt(i); // row index
                pointers[pointers_count++] = pointer++;
                for (int v : adj)
                {
                    out.writeInt(v);
                    pointer++;
                }
            }

            // Write pointers
            out.writeInt(pointers_count); // Write number of pointers
            for (int i = 0; i < pointers_count; i++)
            {
                out.writeInt(pointers[i]);
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
