package graphdivider.view.ui.graph;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

/**
 * Utility for coloring vertices by cluster and handling edge cutting.
 * Provides methods for coloring, edge removal, and edge cut calculation.
 */
public final class GraphColoring
{
    // Prevent instantiation of utility class
    private GraphColoring() {}

    /**
     * Colors vertices by cluster and removes edges between different clusters.
     * Vertices in the same cluster get the same color.
     * Edges connecting different clusters are removed and counted.
     * 
     * @param vertices Array of Vertex objects to color.
     * @param clusters Array of cluster indices for each vertex.
     * @param edges List of edges to update (edges between clusters will be removed).
     * @return Number of edges cut (removed).
     * @throws IllegalArgumentException if input arrays are null or of different lengths.
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public static int colorVertices(Vertex[] vertices, int[] clusters, List<Edge> edges)
    {
        if (vertices == null || clusters == null || vertices.length != clusters.length)
        {
            throw new IllegalArgumentException("Vertices and clusters must be non-null and of the same length.");
        }

        // Map cluster index to color
        Map<Integer, Color> clusterColors = generateClusterColors(clusters);

        // Set color for each vertex
        for (int i = 0; i < vertices.length; i++)
        {
            Color clusterColor = clusterColors.get(clusters[i]);
            vertices[i].setColor(clusterColor);
        }

        // Remove edges between different clusters, count them
        final int[] cutEdges = {0};
        Runnable removeEdgesTask = () ->
        {
            java.util.Iterator<Edge> it = edges.iterator();
            while (it.hasNext())
            {
                Edge edge = it.next();
                int cluster1 = clusters[edge.getVertex1().getId()];
                int cluster2 = clusters[edge.getVertex2().getId()];
                if (cluster1 != cluster2)
                {
                    it.remove();
                    edge.dispose();
                    cutEdges[0]++;
                }
            }
        };
        // Ensure edge removal runs on the EDT for thread safety
        if (SwingUtilities.isEventDispatchThread())
        {
            removeEdgesTask.run();
        } 
        else
        {
            try
            {
                SwingUtilities.invokeAndWait(removeEdgesTask);
            } 
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return cutEdges[0];
    }

    /**
     * Counts the number of edges between different clusters (does not remove them).
     * 
     * @param vertices Array of Vertex objects.
     * @param clusters Array of cluster indices for each vertex.
     * @param edges List of edges to check.
     * @return Number of edges that connect different clusters.
     * @throws IllegalArgumentException if input arrays are null or of different lengths.
     */
    public static int calculateEdgesCut(Vertex[] vertices, int[] clusters, List<Edge> edges)
    {
        if (vertices == null || clusters == null || vertices.length != clusters.length)
        {
            throw new IllegalArgumentException("Vertices and clusters must be non-null and of the same length.");
        }
        int cutEdges = 0;
        for (Edge edge : edges)
        {
            int cluster1 = clusters[edge.getVertex1().getId()];
            int cluster2 = clusters[edge.getVertex2().getId()];
            if (cluster1 != cluster2)
            {
                cutEdges++;
            }
        }
        return cutEdges;
    }

    /**
     * Generates a color for each unique cluster index.
     * Colors are spread using HSB for visual distinction.
     * 
     * @param clusters Array of cluster indices.
     * @return Map from cluster index to Color.
     */
    private static Map<Integer, Color> generateClusterColors(int[] clusters)
    {
        Map<Integer, Color> clusterColors = new HashMap<>();
        int[] uniqueClusters = Arrays.stream(clusters).distinct().toArray();
        int clusterCount = uniqueClusters.length;

        // Use HSB to spread colors
        for (int i = 0; i < clusterCount; i++)
        {
            float hue = (float) i / clusterCount;
            clusterColors.put(uniqueClusters[i], Color.getHSBColor(hue, 0.8f, 0.9f));
        }

        return clusterColors;
    }
}