package graphdivider.view.ui;

import graphdivider.view.ui.graph.Vertex;

import javax.swing.*;
import java.awt.*;

/**
 * Main graph display panel.
 * Occupies the center area of the Frame.
 * Currently renders a few sample vertices.
 */
public class Graph extends JPanel
{
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

        // Add vertices to the panel
        add(v1);
        add(v2);
        add(v3);
    }

    /**
     * Paints the component.
     * Future: custom graph drawing logic (edges, dynamic layout) goes here.
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        // Future: custom graph drawing logic (edges, dynamic layout) goes here
    }
}

