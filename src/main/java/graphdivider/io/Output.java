package graphdivider.io;

import graphdivider.model.CSRmatrix;
import graphdivider.model.GraphModel;

import java.io.*;

/**
 * Utility class for writing graph partition assignments to a text or binary file.
 * Supports both human-readable and compact binary formats.
 */
public final class Output
{
    /**
     * Saves the partitioned graph data in a text format (.txt).
     * Writes partition info, graph structure, and adjacency data.
     *
     * @param file Output file to write to.
     * @param numParts Number of partitions.
     * @param edgesCut Number of edges cut by the partition.
     * @param marginKept Margin kept by the partition.
     * @param graphModel The original graph model.
     * @param adjacencyDivided The partitioned adjacency matrix.
     * @throws IOException If an I/O error occurs.
     */
    public static void writeText(File file, int numParts, int edgesCut, double marginKept, GraphModel graphModel, CSRmatrix adjacencyDivided) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            // Write partitioning result summary
            writer.write(numParts + " " + edgesCut + " " + String.format("%.2f", marginKept));
            writer.newLine();

            // Write graph structure
            writer.write(String.valueOf(graphModel.getMaxVerticesPerRow()));
            writer.newLine();

            writer.write(arrayToSemicolonString(graphModel.getRowPositions()));
            writer.newLine();

            writer.write(arrayToSemicolonString(graphModel.getRowStartIndices()));
            writer.newLine();

            // Write adjacency data and pointers
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

    /**
     * Saves the partitioned graph data in a binary format (.bin).
     * Writes partition info, graph structure, and adjacency data in binary.
     *
     * @param file Output file to write to.
     * @param numParts Number of partitions.
     * @param edgesCut Number of edges cut by the partition.
     * @param marginKept Margin kept by the partition.
     * @param graphModel The original graph model.
     * @param adjacencyDivided The partitioned adjacency matrix.
     * @throws IOException If an I/O error occurs.
     */
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

        testReadBinary(file);
    }

    /**
     * Converts an int array to a semicolon-separated string.
     *
     * @param arr Array to convert.
     * @return Semicolon-separated string of array values.
     */
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

    /**
     * Test method to read and print the content of a binary file.
     * Prints all fields to the console for verification.
     *
     * @param file File to read from.
     * @throws IOException If an I/O error occurs.
     */
    public static void testReadBinary(File file) throws IOException
    {
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_RESET = "\u001B[0m";

        try (DataInputStream in = new DataInputStream(new FileInputStream(file)))
        {
            System.out.println(ANSI_CYAN + "\t\tBINARY FILE CONTENT:" + ANSI_RESET);
            int numParts = in.readInt();
            int edgesCut = in.readInt();
            double marginKept = in.readDouble();
            System.out.println(ANSI_CYAN + "\tnumParts: " + numParts + ANSI_RESET);
            System.out.println(ANSI_CYAN + "\tedgesCut: " + edgesCut + ANSI_RESET);
            System.out.println(ANSI_CYAN + "\tmarginKept: " + marginKept + ANSI_RESET);

            int maxVerticesPerRow = in.readInt();
            System.out.println(ANSI_CYAN + "\tmaxVerticesPerRow: " + maxVerticesPerRow + ANSI_RESET);

            int rowPositionsLen = in.readInt();
            System.out.print(ANSI_CYAN + "\trowPositions: ");
            for (int i = 0; i < rowPositionsLen; i++)
            {
                System.out.print(in.readInt() + (i < rowPositionsLen - 1 ? "; " : ""));
            }
            System.out.println(ANSI_RESET);

            int rowStartIndicesLen = in.readInt();
            System.out.print(ANSI_CYAN + "\trowStartIndices: ");
            for (int i = 0; i < rowStartIndicesLen; i++)
            {
                System.out.print(in.readInt() + (i < rowStartIndicesLen - 1 ? "; " : ""));
            }
            System.out.println(ANSI_RESET);

            int totalAdjacencyElements = in.readInt();
            System.out.println(ANSI_CYAN + "\ttotalAdjacencyElements: " + totalAdjacencyElements + ANSI_RESET);
            System.out.print(ANSI_CYAN + "\tAdjacency data: ");
            for (int i = 0; i < totalAdjacencyElements; i++)
            {
                System.out.print(in.readInt() + (i < totalAdjacencyElements - 1 ? "; " : ""));
            }
            System.out.println(ANSI_RESET);

            int pointersCount = in.readInt();
            System.out.println(ANSI_CYAN + "\tpointersCount: " + pointersCount + ANSI_RESET);
            System.out.print(ANSI_CYAN + "\tPointers: ");
            for (int i = 0; i < pointersCount; i++)
            {
                System.out.print(in.readInt() + (i < pointersCount - 1 ? "; " : ""));
            }
            System.out.println(ANSI_RESET);
        }
    }
}
