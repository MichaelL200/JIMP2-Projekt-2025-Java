package graphdivider.view.ui;

import graphdivider.model.GraphModel;
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
     * Constructs the graph panel.
     * Registers a theme change listener to update edge colors when the theme changes.
     */
    public Graph()
    {
        setLayout(null);      // Use absolute positioning for vertices
        setOpaque(false);     // Allow background to show through

        // Register a listener to update edge colors when the theme changes
        Theme.addThemeChangeListener(() ->
        {
            for (Edge edge : edges)
            {
                edge.updateEdgeColor();
            }
            repaint();
        });
    }

    /**
     * Displays the graph from the given GraphModel.
     * Clears any previous vertices.
     *
     * @param model the GraphModel containing the vertices
     */
    public void displayGraph(GraphModel model)
    {
        // Remove old vertices
        for (Vertex v : vertices)
        {
            remove(v);
        }
        vertices.clear();

        int vertexCount = model.getRowPositions().length;
        int[] rowStartIndices = model.getRowStartIndices();
        int[] rowPositions = model.getRowPositions();

        if (vertexCount == 0)
        {
            repaint();
            return;
        }

        int vertexDiameter = 40;
        int spacing = 20;
        int margin = 10; // left margin
        int rowSpacing = 60; // vertical space between rows

        // Determine which rows are actually used (contain at least one vertex)
        java.util.Set<Integer> usedRows = new java.util.HashSet<>();
        for (int i = 0; i < vertexCount; i++)
        {
            // Find the row this vertex belongs to
            int row = 0;
            for (int r = 0; r < rowStartIndices.length; r++)
            {
                int start = rowStartIndices[r];
                int end = (r + 1 < rowStartIndices.length) ? rowStartIndices[r + 1] : vertexCount;
                if (i >= start && i < end)
                {
                    row = r;
                    break;
                }
            }
            usedRows.add(row);
        }

        // Map actual row indices to visual row indices (skip empty rows)
        java.util.Map<Integer, Integer> rowToVisualRow = new java.util.HashMap<>();
        int visualRow = 0;
        for (int r = 0; r < rowStartIndices.length; r++)
        {
            if (usedRows.contains(r))
            {
                rowToVisualRow.put(r, visualRow++);
            }
        }

        // For each vertex, determine its row and column, using only non-empty rows
        for (int i = 0; i < vertexCount; i++)
        {
            // Find the row this vertex belongs to
            int row = 0;
            for (int r = 0; r < rowStartIndices.length; r++)
            {
                int start = rowStartIndices[r];
                int end = (r + 1 < rowStartIndices.length) ? rowStartIndices[r + 1] : vertexCount;
                if (i >= start && i < end)
                {
                    row = r;
                    break;
                }
            }
            int col = rowPositions[i];
            int visualRowIdx = rowToVisualRow.get(row);

            int x = margin + col * (vertexDiameter + spacing);
            int y = 20 + visualRowIdx * rowSpacing;

            Vertex vertex = new Vertex(i, Vertex.DEFAULT_BLUE, vertexDiameter);
            vertex.setBounds(x, y, vertexDiameter, vertexDiameter);
            vertices.add(vertex);
            add(vertex);
        }

        revalidate();
        repaint();
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

