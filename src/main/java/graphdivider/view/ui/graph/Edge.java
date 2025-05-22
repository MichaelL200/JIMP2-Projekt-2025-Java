package graphdivider.view.ui.graph;

import graphdivider.view.ui.Theme;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents an edge (connection) between two vertices in the graph visualization.
 * Handles drawing itself and adapts its color to the current theme.
 * All edges are tracked for easy global updates (e.g., theme changes).
 */
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

    /**
     * Constructs an edge between two vertices and registers for theme updates.
     *
     * @param v1 The first vertex
     * @param v2 The second vertex
     */
    public Edge(Vertex v1, Vertex v2)
    {
        this.v1 = v1;
        this.v2 = v2;
        updateEdgeColor();
        Theme.addThemeChangeListener(themeListener);
        allEdges.add(this);
    }

    /**
     * Updates the color of all edges in the application.
     * Should be called when the theme changes globally.
     */
    public static void updateAllEdgesColor()
    {
        for (Edge edge : allEdges)
        {
            edge.updateEdgeColor();
        }
    }

    /**
     * Draws the edge as a line between the centers of its two vertices.
     *
     * @param g The Graphics2D context to draw with
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
     * Updates the edge color based on the current theme.
     * Uses white for dark theme, black for light theme.
     */
    public void updateEdgeColor()
    {
        edgeColor = Theme.isDarkPreferred() ? Color.WHITE : Color.BLACK;
    }

    /**
     * Called when the theme changes. Updates the edge color and repaints the connected vertices.
     */
    private void onThemeChanged()
    {
        updateEdgeColor();
        v1.repaint();
        v2.repaint();
    }

    /**
     * Cleans up resources when the edge is no longer needed.
     * Unregisters the theme listener and removes itself from the global edge list.
     */
    public void dispose()
    {
        Theme.removeThemeChangeListener(themeListener);
        allEdges.remove(this);
    }
}
