package graphdivider.view.ui.graph;

import graphdivider.view.ui.Theme;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// One edge (connection) between two vertices
public final class Edge
{
    // All edges for global color update
    private static final List<Edge> allEdges = new CopyOnWriteArrayList<>();

    // Connected vertices
    private final Vertex v1;
    private final Vertex v2;
    // Edge color (theme-dependent)
    private Color edgeColor;
    // Listener for theme changes
    private final Runnable themeListener = this::onThemeChanged;

    // Create edge and register for theme updates
    public Edge(Vertex v1, Vertex v2)
    {
        this.v1 = v1;
        this.v2 = v2;
        updateEdgeColor();
        Theme.addThemeChangeListener(themeListener);
        allEdges.add(this);
    }

    // Update color for all edges
    public static void updateAllEdgesColor()
    {
        for (Edge edge : allEdges)
        {
            edge.updateEdgeColor();
        }
    }

    // Get default edge color (theme-aware)
    public static Color getDefaultEdgeColor()
    {
        return Theme.isDarkPreferred() ? Color.WHITE : Color.BLACK;
    }

    // Draw edge as a line between vertex centers
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

    // Update edge color for current theme
    public void updateEdgeColor()
    {
        edgeColor = getDefaultEdgeColor();
    }

    // Called on theme change
    private void onThemeChanged()
    {
        updateEdgeColor();
        repaintVertices();
    }

    // Repaint both vertices
    private void repaintVertices()
    {
        v1.repaint();
        v2.repaint();
    }

    // Remove edge and unregister listener
    public void dispose()
    {
        allEdges.remove(this);
        Theme.removeThemeChangeListener(themeListener);
    }

    // Get first vertex
    public Vertex getVertex1()
    {
        return v1;
    }

    // Get second vertex
    public Vertex getVertex2()
    {
        return v2;
    }
}
