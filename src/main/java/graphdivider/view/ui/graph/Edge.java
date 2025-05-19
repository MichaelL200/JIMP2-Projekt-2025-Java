package graphdivider.view.ui.graph;

import graphdivider.view.ui.Theme;

import java.awt.*;

/**
 * Represents an edge connecting two vertices in the graph.
 */
public class Edge
{
    private final Vertex v1;
    private final Vertex v2;
    private Component parent; // Reference to parent for repaint

    private static final java.util.List<Edge> allEdges = new java.util.concurrent.CopyOnWriteArrayList<>();

    private final Runnable themeListener = this::onThemeChanged;

    // Store the current color of the edge
    private Color edgeColor;

    public Edge(Vertex v1, Vertex v2)
    {
        this.v1 = v1;
        this.v2 = v2;
        updateEdgeColor(); // Set initial color
        Theme.addThemeChangeListener(themeListener);
        allEdges.add(this);
    }

    /**
     * Optionally set the parent component for repainting.
     */
    public void setParent(Component parent)
    {
        this.parent = parent;
    }

    /**
     * Draws the edge as a line between the centers of the two vertices.
     * Uses the stored edgeColor.
     */
    public void draw(Graphics2D g)
    {
        int x1 = v1.getX() + v1.getDiameter() / 2;
        int y1 = v1.getY() + v1.getDiameter() / 2;
        int x2 = v2.getX() + v2.getDiameter() / 2;
        int y2 = v2.getY() + v2.getDiameter() / 2;

        g.setColor(edgeColor);
        g.setStroke(new BasicStroke(2));
        g.drawLine(x1, y1, x2, y2);
    }

    /**
     * Update the edge color based on the current theme.
     */
    private void updateEdgeColor() {
        edgeColor = Theme.isDarkPreferred() ? Color.WHITE : Color.BLACK;
    }

    private void onThemeChanged()
    {
        updateEdgeColor();
        if (parent != null) {
            parent.repaint();
        }
    }

    /**
     * Updates the color of all edges by repainting their parent components.
     */
    public static void updateAllEdgesColor() {
        for (Edge edge : allEdges) {
            edge.updateEdgeColor();
            if (edge.parent != null) {
                edge.parent.repaint();
            }
        }
    }

    public Vertex getV1()
    {
        return v1;
    }

    public Vertex getV2()
    {
        return v2;
    }

    /**
     * Call this method to clean up listeners and references when the edge is no longer needed.
     */
    public void dispose() {
        Theme.removeThemeChangeListener(themeListener);
        allEdges.remove(this);
    }
}
