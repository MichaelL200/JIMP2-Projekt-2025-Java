package graphdivider.view.ui;

import graphdivider.view.ui.graph.Vertex;

import javax.swing.*;
import java.awt.*;

/**
 * Main graph display panel. Occupies the center area of the Frame.
 * Currently renders a few sample vertices.
 */
public class Graph extends JPanel
{
    public Graph()
    {
        // Use null layout for absolute positioning of vertices
        setLayout(null);

        // Set the background to transparent
        setOpaque(false);

        // Sample vertices
        Vertex v1 = new Vertex(1, Color.RED,   50);
        Vertex v2 = new Vertex(2, Color.BLUE,  50);
        Vertex v3 = new Vertex(3, Color.GREEN, 50);

        // Position them manually
        v1.setBounds(100, 100, v1.getDiameter(), v1.getDiameter());
        v2.setBounds(200, 150, v2.getDiameter(), v2.getDiameter());
        v3.setBounds(150, 250, v3.getDiameter(), v3.getDiameter());

        // Add to panel
        add(v1);
        add(v2);
        add(v3);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        // Future: custom graph drawing logic (edges, dynamic layout) goes here
    }
}
