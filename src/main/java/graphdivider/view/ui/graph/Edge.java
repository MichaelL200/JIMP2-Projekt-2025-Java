package graphdivider.view.ui.graph;

import graphdivider.view.ui.Theme;

import java.awt.*;

public final class Edge
{
    private final Vertex v1;
    private final Vertex v2;

    private static final java.util.List<Edge> allEdges = new java.util.concurrent.CopyOnWriteArrayList<>();
    private final Runnable themeListener = this::onThemeChanged;
    private Color edgeColor;

    public Edge(Vertex v1, Vertex v2)
    {
        this.v1 = v1;
        this.v2 = v2;
        updateEdgeColor();
        Theme.addThemeChangeListener(themeListener);
        allEdges.add(this);
    }

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

    public void updateEdgeColor()
    {
        edgeColor = Theme.isDarkPreferred() ? Color.WHITE : Color.BLACK;
    }

    private void onThemeChanged()
    {
        updateEdgeColor();
        // Repaint the vertices to update the edge color
        if (v1 != null) v1.repaint();
        if (v2 != null) v2.repaint();
    }

    public static void updateAllEdgesColor()
    {
        for (Edge edge : allEdges)
        {
            edge.updateEdgeColor();
        }
    }

    public void dispose()
    {
        Theme.removeThemeChangeListener(themeListener);
        allEdges.remove(this);
    }
}
