package graphdivider.model;

import java.util.Arrays;
import java.util.Random;

/**
 * Graph clustering utilities using Fiedler vector and k-means.
 * Supports spectral partitioning and balanced k-means clustering.
 */
public final class GraphClusterization
{
    /**
     * Clusters the graph using the Fiedler vector (for 2 parts) or k-means (for p parts).
     *
     * @param eigenResult EigenResult containing eigenvectors.
     * @param p Number of clusters/partitions.
     * @return Array of cluster indices for each vertex (1-based), or null on error.
     */
    public static int[] clusterizeGraph(GraphEigenvalues.EigenResult eigenResult, int p)
    {
        try
        {
            if (p == 2)
            {
                return partitionByFiedlerVector(eigenResult);
            } 
            else
            {
                return clusterizeUsingKMeans(eigenResult, p);
            }
        } 
        catch (Exception e)
        {
            System.err.println("Error during graph clustering: " + e.getMessage());
            return null;
        }
    }

    /**
     * Partitions the graph into two groups using the Fiedler vector.
     * Assigns half the vertices to each group based on sorted Fiedler values.
     *
     * @param eigenResult EigenResult containing eigenvectors.
     * @return Array of cluster indices (1 or 2) for each vertex.
     */
    private static int[] partitionByFiedlerVector(GraphEigenvalues.EigenResult eigenResult)
    {
        double[] fiedlerVector = eigenResult.eigenvectors[1];
        int n = fiedlerVector.length;
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;

        // Sort indices by Fiedler vector value
        java.util.Arrays.sort(indices, java.util.Comparator.comparingDouble(i -> fiedlerVector[i]));

        int[] groupIndices = new int[n];
        int half = n / 2;
        for (int i = 0; i < n; i++)
        {
            if (i < half)
            {
                groupIndices[indices[i]] = 1;
            } 
            else
            {
                groupIndices[indices[i]] = 2;
            }
        }
        return groupIndices;
    }

    /**
     * Performs balanced k-means clustering using the first p eigenvectors.
     *
     * @param eigenResult EigenResult containing eigenvectors.
     * @param p Number of clusters.
     * @return Array of cluster indices (1-based) for each vertex.
     */
    private static int[] clusterizeUsingKMeans(GraphEigenvalues.EigenResult eigenResult, int p)
    {
        int n = eigenResult.eigenvectors[0].length; // Number of vertices
        int dimensions = eigenResult.eigenvectors.length; // Number of eigenvectors used
        double[][] data = new double[n][dimensions];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < dimensions; j++)
                data[i][j] = eigenResult.eigenvectors[j][i];

        int maxIterations = 100;
        double[][] centroids = initializeCentroids(data, p);
        int[] clusters = new int[n];

        int minSize = n / p;
        int extra = n % p; // Some clusters will have one extra

        for (int iteration = 0; iteration < maxIterations; iteration++)
        {
            // Assign vertices to nearest centroid (respect size limits)
            int[] clusterSizes = new int[p];
            boolean[] assigned = new boolean[n];

            for (int i = 0; i < n; i++)
            {
                // Compute distances to all centroids
                double[] distances = new double[p];
                for (int j = 0; j < p; j++)
                {
                    distances[j] = euclideanDistance(data[i], centroids[j]);
                }

                // Try to assign to closest centroid with available capacity
                Integer[] order = new Integer[p];
                for (int j = 0; j < p; j++) order[j] = j;
                Arrays.sort(order, java.util.Comparator.comparingDouble(j -> distances[j]));

                for (int idx = 0; idx < p; idx++)
                {
                    int c = order[idx];
                    int targetSize = minSize + (c < extra ? 1 : 0);
                    if (clusterSizes[c] < targetSize)
                    {
                        clusters[i] = c + 1; // 1-based
                        clusterSizes[c]++;
                        assigned[i] = true;
                        break;
                    }
                }
            }

            // Update centroids
            double[][] newCentroids = new double[p][dimensions];
            int[] newClusterSizes = new int[p];
            for (int i = 0; i < n; i++)
            {
                int cluster = clusters[i] - 1;
                newClusterSizes[cluster]++;
                for (int j = 0; j < dimensions; j++)
                {
                    newCentroids[cluster][j] += data[i][j];
                }
            }
            for (int i = 0; i < p; i++)
            {
                if (newClusterSizes[i] > 0)
                {
                    for (int j = 0; j < dimensions; j++)
                    {
                        newCentroids[i][j] /= newClusterSizes[i];
                    }
                } 
                else
                {
                    newCentroids[i] = data[new Random().nextInt(n)].clone();
                }
            }

            // Check for convergence
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

    /**
     * Initializes centroids for k-means clustering.
     * Evenly spaces centroids along each dimension.
     *
     * @param data Data points (vertices) as [vertex][dimension].
     * @param p Number of clusters.
     * @return Initialized centroids.
     */
    private static double[][] initializeCentroids(double[][] data, int p)
    {
        int n = data.length;
        int dimensions = data[0].length;
        double[][] centroids = new double[p][dimensions];

        // Find min/max for each dimension
        double[] min = new double[dimensions];
        double[] max = new double[dimensions];
        Arrays.fill(min, Double.POSITIVE_INFINITY);
        Arrays.fill(max, Double.NEGATIVE_INFINITY);

        for (int i = 0; i < n; i++)
        {
            for (int d = 0; d < dimensions; d++)
            {
                if (data[i][d] < min[d]) min[d] = data[i][d];
                if (data[i][d] > max[d]) max[d] = data[i][d];
            }
        }

        // Evenly space centroids along each dimension
        for (int c = 0; c < p; c++)
        {
            for (int d = 0; d < dimensions; d++)
            {
                if (p == 1)
                {
                    centroids[c][d] = (min[d] + max[d]) / 2.0;
                } 
                else
                {
                    centroids[c][d] = min[d] + (max[d] - min[d]) * c / (p - 1);
                }
            }
        }

        return centroids;
    }

    /**
     * Computes the Euclidean distance between two vectors.
     *
     * @param vector1 First vector.
     * @param vector2 Second vector.
     * @return Euclidean distance.
     */
    private static double euclideanDistance(double[] vector1, double[] vector2)
    {
        double sum = 0.0;
        for (int i = 0; i < vector1.length; i++)
        {
            sum += Math.pow(vector1[i] - vector2[i], 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * Prints clusters (partitions) indices to the console.
     * Groups vertices by cluster and prints each group.
     *
     * @param clusters Array of cluster indices for each vertex.
     */
    public static void printClusters(int[] clusters)
    {
        final String GREEN = "\u001B[32m";
        final String RESET = "\u001B[0m";

        if (clusters == null || clusters.length == 0)
        {
            System.out.println(GREEN + "No clusters to display." + RESET);
            return;
        }

        // Group vertices by cluster
        java.util.Map<Integer, java.util.List<Integer>> clusterMap = new java.util.HashMap<>();
        for (int vertex = 0; vertex < clusters.length; vertex++)
        {
            int cluster = clusters[vertex];
            clusterMap.computeIfAbsent(cluster, k -> new java.util.ArrayList<>()).add(vertex);
        }

        System.out.println(GREEN + "\t\tCLUSTERS (PARTS):");
        for (var entry : clusterMap.entrySet())
        {
            System.out.print(GREEN + "\tCluster " + entry.getKey() + ": ");
            System.out.print(entry.getValue());
            System.out.println(RESET);
        }
    }

    /**
     * Calculates the margin of clusters (relative size difference).
     * Margin is defined as (max - min) / min * 100%.
     *
     * @param clusters Array of cluster indices for each vertex.
     * @param numParts Number of clusters/partitions.
     * @return Margin as a percentage (0 if any cluster is empty).
     */
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
        return min > 0 ? ((double)(max - min) / min) * 100.0 : 0.0;
    }
}