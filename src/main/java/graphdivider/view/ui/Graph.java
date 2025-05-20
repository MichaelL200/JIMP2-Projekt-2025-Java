package graphdivider.view.ui;

import graphdivider.view.ui.graph.Vertex;
import graphdivider.view.ui.graph.Edge;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Graph extends JPanel
{
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();

    public Graph()
    {
        setLayout(null);
        setOpaque(false);

        Vertex v1 = new Vertex(0, Vertex.DEFAULT_BLUE, 50);
        Vertex v2 = new Vertex(1, Vertex.DEFAULT_BLUE, 50);
        Vertex v3 = new Vertex(2, Vertex.DEFAULT_BLUE, 50);

        v1.setBounds(100, 100, v1.getDiameter(), v1.getDiameter());
        v2.setBounds(200, 150, v2.getDiameter(), v2.getDiameter());
        v3.setBounds(150, 250, v3.getDiameter(), v3.getDiameter());

        add(v1);
        add(v2);
        add(v3);
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);

        Edge e1 = new Edge(v1, v2);
        Edge e2 = new Edge(v2, v3);
        Edge e3 = new Edge(v3, v1);
        // No need to set parent for repainting
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);

        // Listen for theme changes and repaint the panel
        Theme.addThemeChangeListener(() -> {
            // Update all edge colors before repainting
            for (Edge edge : edges) {
                edge.updateEdgeColor();
            }
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        for (Edge edge : edges) {
            edge.draw(g2);
        }
        g2.dispose();
    }

    // No need for refreshTheme() anymore
}
