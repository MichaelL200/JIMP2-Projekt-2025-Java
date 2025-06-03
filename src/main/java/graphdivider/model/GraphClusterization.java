package graphdivider.model;

import java.util.Arrays;
import java.util.Random;

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
            return null;
        }
    }

    // Partition vertices into two groups using the Fiedler vector.
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

    // K-means clustering for spectral clustering (fix: transpose eigenvectors for correct shape)
    private static int[] clusterizeUsingKMeans(GraphEigenvalues.EigenResult eigenResult, int p)
    {
        int n = eigenResult.eigenvectors[0].length; // Number of vertices
        int dimensions = eigenResult.eigenvectors.length; // Number of eigenvectors used (should be p)
        double[][] data = new double[n][dimensions];
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < dimensions; j++)
            {
                data[i][j] = eigenResult.eigenvectors[j][i];
            }
        }

        int maxIterations = 100;
        double[][] centroids = initializeCentroids(data, p);
        int[] clusters = new int[n];

        for (int iteration = 0; iteration < maxIterations; iteration++)
        {
            // Step 1: Assign vertices to the nearest centroid
            for (int i = 0; i < n; i++)
            {
                double minDistance = Double.MAX_VALUE;
                int closestCentroid = -1;
                for (int j = 0; j < p; j++)
                {
                    double distance = euclideanDistance(data[i], centroids[j]);
                    if (distance < minDistance)
                    {
                        minDistance = distance;
                        closestCentroid = j;
                    }
                }
                clusters[i] = closestCentroid + 1; // Use 1-based cluster indices for consistency
            }

            // Step 2: Update centroids
            double[][] newCentroids = new double[p][dimensions];
            int[] clusterSizes = new int[p];

            for (int i = 0; i < n; i++)
            {
                int cluster = clusters[i] - 1;
                clusterSizes[cluster]++;
                for (int j = 0; j < dimensions; j++)
                {
                    newCentroids[cluster][j] += data[i][j];
                }
            }

            for (int i = 0; i < p; i++)
            {
                if (clusterSizes[i] > 0)
                {
                    for (int j = 0; j < dimensions; j++)
                    {
                        newCentroids[i][j] /= clusterSizes[i];
                    }
                } else
                {
                    // Handle empty clusters by reinitializing the centroid
                    newCentroids[i] = data[new Random().nextInt(n)].clone();
                }
            }

            // Step 3: Check for convergence
            boolean converged = true;
            for (int i = 0; i < p; i++)
            {
                if (!Arrays.equals(centroids[i], newCentroids[i]))
                {
                    converged = false;
                    break;
                }
            }

            centroids = newCentroids;
            if (converged) break;
        }

        return clusters;
    }

    private static double[][] initializeCentroids(double[][] data, int p)
    {
        int n = data.length; // Number of data points
        double[][] centroids = new double[p][data[0].length];
        Random random = new Random();

        // Step 1: Randomly select the first centroid
        int firstIndex = random.nextInt(n);
        centroids[0] = data[firstIndex].clone();

        // Step 2: Select remaining centroids using k-means++ logic
        for (int i = 1; i < p; i++)
        {
            double[] distances = new double[n];
            double totalDistance = 0.0;

            // Calculate the distance to the nearest centroid
            for (int j = 0; j < n; j++)
            {
                double minDistance = Double.MAX_VALUE;
                for (int k = 0; k < i; k++)
                {
                    double distance = euclideanDistance(data[j], centroids[k]);
                    minDistance = Math.min(minDistance, distance);
                }
                distances[j] = minDistance * minDistance; // Square the distance
                totalDistance += distances[j];
            }

            // Select the next centroid with weighted probability
            double threshold = random.nextDouble() * totalDistance;
            double cumulativeDistance = 0.0;
            for (int j = 0; j < n; j++)
            {
                cumulativeDistance += distances[j];
                if (cumulativeDistance >= threshold)
                {
                    centroids[i] = data[j].clone();
                    break;
                }
            }
        }

        return centroids;
    }

    // Helper method to calculate Euclidean distance
    private static double euclideanDistance(double[] vector1, double[] vector2)
    {
        double sum = 0.0;
        for (int i = 0; i < vector1.length; i++)
        {
            sum += Math.pow(vector1[i] - vector2[i], 2);
        }
        return Math.sqrt(sum);
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

    // Calculate the margin of the clusters based on their sizes
    public static double calculateMargin(int[] clusters, int numParts)
    {
        int[] clusterSizes = new int[numParts];
        for (int cluster : clusters)
        {
            if (cluster >= 1 && cluster <= numParts)
            {
                clusterSizes[cluster - 1]++;
            }
        }
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int size : clusterSizes)
        {
            if (size < min) min = size;
            if (size > max) max = size;
        }
        return min > 0 ? ((double)(max - min) / min) * 100.0 : 0.0; // <-- percent
    }
}