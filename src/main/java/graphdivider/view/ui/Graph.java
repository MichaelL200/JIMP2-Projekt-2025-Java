package graphdivider.view.ui;

import graphdivider.view.ui.graph.Vertex;
import graphdivider.view.ui.graph.Edge;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main graph display panel.
 * Occupies the center area of the Frame.
 * Currently renders a few sample vertices.
 */
public class Graph extends JPanel
{
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();

    /**
     * Constructs the Graph panel and adds sample vertices.
     */
    public Graph()
    {
        // Use null layout for absolute positioning of vertices
        setLayout(null);

        // Set the background to transparent
        setOpaque(false);

        // Create sample vertices with blue color and IDs
        Vertex v1 = new Vertex(1, Vertex.DEFAULT_BLUE, 50);
        Vertex v2 = new Vertex(2, Vertex.DEFAULT_BLUE, 50);
        Vertex v3 = new Vertex(3, Vertex.DEFAULT_BLUE, 50);

        // Manually position each vertex on the panel
        v1.setBounds(100, 100, v1.getDiameter(), v1.getDiameter());
        v2.setBounds(200, 150, v2.getDiameter(), v2.getDiameter());
        v3.setBounds(150, 250, v3.getDiameter(), v3.getDiameter());

        // Add vertices to the panel and to the list
        add(v1);
        add(v2);
        add(v3);
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);

        // Create sample edges
        edges.add(new Edge(v1, v2));
        edges.add(new Edge(v2, v3));
        edges.add(new Edge(v3, v1));
        // Set parent for each edge so it can repaint this panel on theme change
        for (Edge edge : edges) {
            edge.setParent(this);
        }
    }

    /**
     * Paints the component.
     * Draws edges before painting child components (vertices).
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Draw all edges
        for (Edge edge : edges) {
            edge.draw(g2);
        }
        g2.dispose();
    }

    /**
     * Call this method to force a theme refresh (e.g. after menu bar theme switch).
     */
    public void refreshTheme() {
        repaint();
    }
}
