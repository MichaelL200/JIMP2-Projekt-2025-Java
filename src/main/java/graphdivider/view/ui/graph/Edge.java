package graphdivider.view.ui.graph;

import graphdivider.view.ui.Theme;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Represents an edge (connection) between two vertices in the graph visualization.
public final class Edge
{
    // Static list of all edges for global updates
    private static final List<Edge> allEdges = new CopyOnWriteArrayList<>();
    private final Vertex v2;
    // The two vertices this edge connects
    private final Vertex v1;
    // Current color of the edge (depends on theme)
    private Color edgeColor;
    // Listener to update this edge's color when the theme changes
    private final Runnable themeListener = this::onThemeChanged;

    // Constructs an edge between two vertices and registers for theme updates.
    public Edge(Vertex v1, Vertex v2)
    {
        this.v1 = v1;
        this.v2 = v2;
        updateEdgeColor();
        Theme.addThemeChangeListener(themeListener);
        allEdges.add(this);
    }

    // Updates the color of all edges in the application.
    public static void updateAllEdgesColor()
    {
        for (Edge edge : allEdges)
        {
            edge.updateEdgeColor();
        }
    }

    // Draws the edge as a line between the centers of its two vertices.
    public void draw(Graphics2D g)
    {
        int x1 = v1.getX() + v1.getDiameter() / 2;
        int y1 = v1.getY() + v1.getDiameter() / 2;
        int x2 = v2.getX() + v2.getDiameter() / 2;
        int y2 = v2.getY() + v2.getDiameter() / 2;

        g.setColor(edgeColor);
        g.setStroke(new BasicStroke(1.3f));
        g.drawLine(x1, y1, x2, y2);
    }

    // Updates the edge color based on the current theme.
    public void updateEdgeColor()
    {
        edgeColor = Theme.isDarkPreferred() ? Color.WHITE : Color.BLACK;
    }

    // Called when the theme changes. Updates the edge color and repaints the connected vertices.
    private void onThemeChanged()
    {
        updateEdgeColor();
        v1.repaint();
        v2.repaint();
    }

    // Cleans up resources when the edge is no longer needed.
    public void dispose()
    {
        Theme.removeThemeChangeListener(themeListener);
        allEdges.remove(this);
    }

    // Getters for the connected vertices
    public Vertex getVertex1()
    {
        return v1;
    }
    public Vertex getVertex2()
    {
        return v2;
    }
}
