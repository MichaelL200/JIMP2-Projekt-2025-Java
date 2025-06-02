package graphdivider.view.ui.graph;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// Colors vertices based on their cluster indices in a graph visualization.
// Deletes edges between vertices in different clusters.
public final class GraphColoring
{
    // Colors vertices based on their cluster indices and removes edges between different clusters.
    public static void colorVertices(Vertex[] vertices, int[] clusters, java.util.List<Edge> edges)
    {
        if (vertices == null || clusters == null || vertices.length != clusters.length)
        {
            throw new IllegalArgumentException("Vertices and clusters must be non-null and of the same length.");
        }

        // Generate distinct colors for each cluster
        Map<Integer, Color> clusterColors = generateClusterColors(clusters);

        // Assign colors to vertices based on their cluster
        for (int i = 0; i < vertices.length; i++)
        {
            Color clusterColor = clusterColors.get(clusters[i]);
            vertices[i].setColor(clusterColor);
        }

        // Remove edges between vertices in different clusters
        edges.removeIf(edge ->
        {
            int cluster1 = clusters[edge.getVertex1().getId()];
            int cluster2 = clusters[edge.getVertex2().getId()];
            if (cluster1 != cluster2)
            {
                edge.dispose(); // Clean up resources
                return true;    // Remove edge
            }
            return false;
        });
    }

    // Generates distinct colors for each cluster index.
    private static Map<Integer, Color> generateClusterColors(int[] clusters)
    {
        Map<Integer, Color> clusterColors = new HashMap<>();
        int clusterCount = (int) Arrays.stream(clusters).distinct().count();

        // Generate distinct colors using HSB color model
        for (int i = 0; i < clusterCount; i++)
        {
            float hue = (float) i / clusterCount; // Spread colors evenly
            clusterColors.put(i + 1, Color.getHSBColor(hue, 0.8f, 0.9f)); // Bright colors
        }

        return clusterColors;
    }
}