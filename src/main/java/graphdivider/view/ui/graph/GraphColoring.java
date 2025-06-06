package graphdivider.view.ui.graph;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Utility for coloring vertices and cutting edges
public final class GraphColoring
{
    // Prevent instantiation
    private GraphColoring() {}

    // Color vertices by cluster and remove inter-cluster edges
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
        int cutEdges = 0;
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
                cutEdges++;
            }
        }
        return cutEdges;
    }

    // Count edges between different clusters (do not remove)
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

    // Generate a color for each cluster index
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