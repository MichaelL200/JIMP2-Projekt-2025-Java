package graphdivider.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for loading cluster assignments and related files for partitioned graphs.
 * Supports both text and binary partitioned files.
 */
public class Input
{
    /**
     * Loads cluster assignments for a given partitioned graph file (wynik*.csrrg).
     * Reads assignments from the corresponding przypisania*.txt file.
     *
     * @param partitionedFile The partitioned graph file (.csrrg).
     * @return Array of cluster indices for each vertex.
     * @throws IOException If the assignment file cannot be read or is malformed.
     */
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
            } 
            else
            {
                baseNumber = name.replaceAll("wynik \\((\\d+)\\)\\.csrrg", "$1");
            }
        } 
        else
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

    /**
     * Loads cluster assignments for a given partitioned binary file (wynik*.bin).
     * Reads assignments from the corresponding przypisania*.txt file.
     *
     * @param partitionedBinFile The partitioned binary file (.bin).
     * @return Array of cluster indices for each vertex.
     * @throws IOException If the assignment file cannot be read or is malformed.
     */
    public static int[] loadClustersForBin(File partitionedBinFile) throws IOException
    {
        String name = partitionedBinFile.getName();
        String baseNumber = "";
        // Match "wynik.bin" or "wynik (N).bin"
        if (name.matches("wynik( \\(\\d+\\))?\\.bin"))
        {
            if (name.equals("wynik.bin"))
            {
                baseNumber = "";
            } 
            else
            {
                baseNumber = name.replaceAll("wynik \\((\\d+)\\)\\.bin", "$1");
            }
        } 
        else
        {
            throw new FileNotFoundException("Unrecognized partitioned binary file: " + name);
        }

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

    /**
     * Returns the base graph file for a given partitioned file (.csrrg).
     *
     * @param partitionedFile The partitioned graph file.
     * @return File object for the base graph file.
     * @throws IllegalArgumentException If the file name is not recognized.
     */
    public static File getBaseGraphFile(File partitionedFile)
    {
        String name = partitionedFile.getName();
        String baseNumber = "";
        if (name.matches("wynik( \\(\\d+\\))?\\.csrrg"))
        {
            if (name.equals("wynik.csrrg"))
            {
                baseNumber = "";
            } 
            else
            {
                baseNumber = name.replaceAll("wynik \\((\\d+)\\)\\.csrrg", "$1");
            }
        } 
        else
        {
            throw new IllegalArgumentException("Unrecognized partitioned file: " + name);
        }
        String graphFileName = baseNumber.isEmpty() ? "graf.csrrg" : "graf" + baseNumber + ".csrrg";
        return new File("src/main/resources/graphs/" + graphFileName);
    }

    /**
     * Returns the base graph file for a given partitioned binary file (.bin).
     *
     * @param partitionedFile The partitioned binary file.
     * @return File object for the base graph file.
     * @throws IllegalArgumentException If the file name is not recognized.
     */
    public static File getBaseGraphFileForBin(File partitionedFile)
    {
        String name = partitionedFile.getName();
        String baseNumber = "";
        if (name.matches("wynik( \\(\\d+\\))?\\.bin"))
        {
            if (name.equals("wynik.bin"))
            {
                baseNumber = "";
            } 
            else
            {
                baseNumber = name.replaceAll("wynik \\((\\d+)\\)\\.bin", "$1");
            }
        } 
        else
        {
            throw new IllegalArgumentException("Unrecognized partitioned binary file: " + name);
        }
        String graphFileName = baseNumber.isEmpty() ? "graf.csrrg" : "graf" + baseNumber + ".csrrg";
        return new File("src/main/resources/graphs/" + graphFileName);
    }

    /**
     * Returns the assignment file for a given partitioned binary file (.bin).
     *
     * @param partitionedFile The partitioned binary file.
     * @return File object for the assignment file.
     * @throws IllegalArgumentException If the file name is not recognized.
     */
    public static File getAssignmentFileForBin(File partitionedFile)
    {
        String name = partitionedFile.getName();
        String baseNumber = "";
        if (name.matches("wynik( \\(\\d+\\))?\\.bin"))
        {
            if (name.equals("wynik.bin"))
            {
                baseNumber = "";
            } 
            else
            {
                baseNumber = name.replaceAll("wynik \\((\\d+)\\)\\.bin", "$1");
            }
        } 
        else
        {
            throw new IllegalArgumentException("Unrecognized partitioned binary file: " + name);
        }
        String assignmentFileName = baseNumber.isEmpty() ? "przypisania.txt" : "przypisania (" + baseNumber + ").txt";
        // If your assignment files are .txt, change .csrrg to .txt above
        return new File("src/main/resources/divided_graphs/" + assignmentFileName);
    }
}