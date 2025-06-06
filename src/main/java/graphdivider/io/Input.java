package graphdivider.io;

import java.io.*;
import java.util.*;

public class Input
{
    // Loads cluster assignments for a given partitioned graph file (wynik*.csrrg)
    public static int[] loadClustersForGraph(File partitionedFile) throws IOException
    {
        String name = partitionedFile.getName();
        String baseNumber = "";
        // Match "wynik.csrrg" or "wynik (N).csrrg"
        if (name.matches("wynik( \\(\\d+\\))?\\.csrrg"))
        {
            if (name.equals("wynik.csrrg"))
            {
                baseNumber = "";
            } else
            {
                baseNumber = name.replaceAll("wynik \\((\\d+)\\)\\.csrrg", "$1");
            }
        } else
        {
            throw new FileNotFoundException("Unrecognized partitioned file: " + name);
        }

        String graphFileName = baseNumber.isEmpty() ? "graf.csrrg" : "graf" + baseNumber + ".csrrg";
        String assignmentFileName = baseNumber.isEmpty() ? "przypisania.txt" : "przypisania (" + baseNumber + ").txt";

        File assignmentFile = new File("src/main/resources/divided_graphs/" + assignmentFileName);
        List<Integer> clusters = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(assignmentFile)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("=>");
                if (parts.length == 2)
                {
                    String clusterStr = parts[1].replaceAll("[^0-9]", "");
                    clusters.add(Integer.parseInt(clusterStr));
                }
            }
        }
        return clusters.stream().mapToInt(i -> i).toArray();
    }

    // Returns the base graph file for a given partitioned file
    public static File getBaseGraphFile(File partitionedFile)
    {
        String name = partitionedFile.getName();
        String baseNumber = "";
        if (name.matches("wynik( \\(\\d+\\))?\\.csrrg"))
        {
            if (name.equals("wynik.csrrg"))
            {
                baseNumber = "";
            } else
            {
                baseNumber = name.replaceAll("wynik \\((\\d+)\\)\\.csrrg", "$1");
            }
        } else
        {
            throw new IllegalArgumentException("Unrecognized partitioned file: " + name);
        }
        String graphFileName = baseNumber.isEmpty() ? "graf.csrrg" : "graf" + baseNumber + ".csrrg";
        return new File("src/main/resources/graphs/" + graphFileName);
    }
}