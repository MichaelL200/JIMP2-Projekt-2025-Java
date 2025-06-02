package graphdivider.model;

// Clusterization of a graph using the Fiedler vector and k-means clustering
public final class GraphClusterization
{
    // Clusters the graph based on the Fiedler vector and k-means clustering.
    public static int[] clusterizeGraph(GraphEigenvalues.EigenResult eigenResult, int p)
    {
        try
        {
            if (p == 2)
            {
                return partitionByFiedlerVector(eigenResult);
            } else
            {
                return clusterizeUsingKMeans(eigenResult, p);
            }
        } catch (Exception e)
        {
            System.err.println("Error during graph clustering: " + e.getMessage());
            return null; // Return null in case of an error
        }
    }

    // Helper method: Partition vertices into two groups using the Fiedler vector.
    private static int[] partitionByFiedlerVector(GraphEigenvalues.EigenResult eigenResult)
    {
        double[] fiedlerVector = eigenResult.eigenvectors[1];
        int[] groupIndices = new int[fiedlerVector.length];

        System.out.println("Graph partitioning based on the Fiedler vector:");
        for (int i = 0; i < fiedlerVector.length; i++)
        {
            groupIndices[i] = fiedlerVector[i] < 0 ? 1 : 2; // Store group index
            System.out.println("Vertex " + i + ": Cluster " + groupIndices[i]);
        }

        return groupIndices;
    }

    // Helper method: Placeholder for k-means clustering implementation.
    private static int[] clusterizeUsingKMeans(GraphEigenvalues.EigenResult eigenResult, int p)
    {
        System.out.println("K-means clustering with " + p + " clusters is not yet implemented.");
        return null; // Placeholder return value
    }

    // Print the clusters (partitions) indices
    public static void printClusters(int[] clusters)
    {
        final String COLOR = "\u001B[32m"; // Green
        final String ANSI_RESET = "\u001B[0m";

        if (clusters == null || clusters.length == 0)
        {
            System.out.println("No clusters to display.");
            return;
        }

        System.out.println("Clusters:");
        for (int i = 0; i < clusters.length; i++)
        {
            System.out.println(COLOR + "Vertex " + i + ": Cluster " + clusters[i] + ANSI_RESET);
        }
    }
}