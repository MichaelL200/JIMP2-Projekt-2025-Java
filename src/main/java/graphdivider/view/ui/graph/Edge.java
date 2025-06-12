package graphdivider.view.ui.graph;

import graphdivider.view.Theme;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents an edge (connection) between two vertices in the graph view.
 * Handles drawing, color updates, and theme responsiveness.
 */
public final class Edge
{
    // List of all edges for global color update (thread-safe)
    private static final List<Edge> allEdges = new CopyOnWriteArrayList<>();

    // First connected vertex
    private final Vertex v1;
    // Second connected vertex
    private final Vertex v2;
    // Edge color (theme-dependent)
    private Color edgeColor;
    // Listener for theme changes (registered on construction)
    private final Runnable themeListener = this::onThemeChanged;

    /**
     * Creates an edge between two vertices and registers for theme updates.
     *
     * @param v1 First vertex.
     * @param v2 Second vertex.
     */
    public Edge(Vertex v1, Vertex v2)
    {
        this.v1 = v1;
        this.v2 = v2;
        updateEdgeColor();
        Theme.addThemeListener(themeListener);
        allEdges.add(this);
    }

    /**
     * Updates the color for all edges (static utility).
     */
    public static void updateAllEdgesColor()
    {
        for (Edge edge : allEdges)
        {
            edge.updateEdgeColor();
        }
    }

    /**
     * Gets the default edge color based on the current theme.
     *
     * @return Color for edges (white for dark theme, black for light theme).
     */
    public static Color getDefaultEdgeColor()
    {
        return Theme.isDarkThemeActive() ? Color.WHITE : Color.BLACK;
    }

    /**
     * Draws the edge as a line between the centers of the two vertices.
     *
     * @param g Graphics2D context to draw on.
     */
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

    /**
     * Updates the edge color for the current theme.
     */
    public void updateEdgeColor()
    {
        edgeColor = getDefaultEdgeColor();
    }

    /**
     * Called when the theme changes.
     * Updates edge color and repaints connected vertices.
     */
    private void onThemeChanged()
    {
        updateEdgeColor();
        repaintVertices();
    }

    /**
     * Repaints both connected vertices to ensure edge color is updated visually.
     */
    private void repaintVertices()
    {
        v1.repaint();
        v2.repaint();
    }

    /**
     * Removes this edge and unregisters its theme listener.
     * Should be called when the edge is deleted.
     */
    public void dispose()
    {
        allEdges.remove(this);
        Theme.removeThemeListener(themeListener);
    }

    /**
     * Gets the first connected vertex.
     *
     * @return First vertex.
     */
    public Vertex getVertex1()
    {
        return v1;
    }

    /**
     * Gets the second connected vertex.
     *
     * @return Second vertex.
     */
    public Vertex getVertex2()
    {
        return v2;
    }
}