package graphdivider.view.ui;

import graphdivider.view.ui.graph.Edge;
import graphdivider.view.ui.graph.Vertex;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JPanel responsible for displaying the graph visualization.
 * Manages vertices and edges, and handles their rendering.
 * <p>
 * This class is intended to be the main drawing area for the graph.
 * Vertices are added as child components, while edges are drawn manually.
 */
public final class Graph extends JPanel
{
    // List of vertices in the graph (each is a component)
    private final List<Vertex> vertices = new ArrayList<>();
    // List of edges in the graph (drawn manually)
    private final List<Edge> edges = new ArrayList<>();

    /**
     * Constructs a sample graph with three vertices and three edges.
     * Registers a theme change listener to update edge colors when the theme changes.
     *
     * This constructor is currently for demonstration and testing purposes.
     * In a full application, vertices and edges would be loaded dynamically.
     */
    public Graph()
    {
        setLayout(null);      // Use absolute positioning for vertices
        setOpaque(false);     // Allow background to show through

        // Create three sample vertices with default color and size
        Vertex v1 = new Vertex(0, Vertex.DEFAULT_BLUE, 50);
        Vertex v2 = new Vertex(1, Vertex.DEFAULT_BLUE, 50);
        Vertex v3 = new Vertex(2, Vertex.DEFAULT_BLUE, 50);

        // Position the vertices on the panel (x, y, width, height)
        v1.setBounds(100, 100, v1.getDiameter(), v1.getDiameter());
        v2.setBounds(200, 150, v2.getDiameter(), v2.getDiameter());
        v3.setBounds(150, 250, v3.getDiameter(), v3.getDiameter());

        // Add vertices to the panel and internal list for management
        add(v1);
        add(v2);
        add(v3);
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);

        // Create edges connecting the vertices (undirected for demonstration)
        Edge e1 = new Edge(v1, v2);
        Edge e2 = new Edge(v2, v3);
        Edge e3 = new Edge(v3, v1);

        // Add edges to the internal list for drawing
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);

        // Register a listener to update edge colors when the theme changes
        Theme.addThemeChangeListener(() ->
        {
            // Update the color of each edge to match the new theme
            for (Edge edge : edges)
            {
                edge.updateEdgeColor();
            }
            // Repaint the panel to reflect color changes
            repaint();
        });
    }

    /**
     * Paints the graph edges on the panel.
     * Vertices are painted by their own components (handled by Swing).
     *
     * @param g the Graphics context used for drawing
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Draw all edges before the vertices (so edges appear behind)
        for (Edge edge : edges)
        {
            edge.draw(g2);
        }
        g2.dispose();
    }
}

