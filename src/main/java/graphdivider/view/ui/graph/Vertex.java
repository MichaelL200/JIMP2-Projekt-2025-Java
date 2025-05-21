package graphdivider.view.ui.graph;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a single vertex (node) in a graph visualization.
 * Displays as a colored circle with an ID label centered inside.
 */
public final class Vertex extends JComponent
{
    /**
     * Default blue color for vertices.
     */
    public static final Color DEFAULT_BLUE = new Color(173, 216, 230);
    /**
     * Diameter of the vertex circle (in pixels).
     */
    private final int diameter;
    /**
     * Unique identifier for the vertex (displayed as label).
     */
    private int id;
    /**
     * Fill color of the vertex circle.
     */
    private Color color;

    /**
     * Constructs a vertex with the given id, color, and diameter.
     * @param id The identifier to display inside the vertex
     * @param color The fill color of the vertex
     * @param diameter The diameter (in pixels) of the vertex
     */
    public Vertex(int id, Color color, int diameter)
    {
        this.id = id;
        this.color = color;
        this.diameter = diameter;
        Dimension size = new Dimension(diameter, diameter);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }

    /**
     * Paints the vertex as a filled circle with a centered label.
     * Uses anti-aliasing for smooth edges.
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try
        {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(0, 0, diameter, diameter);

            String label = String.valueOf(id);
            FontMetrics fm = g2.getFontMetrics();
            int x = (diameter - fm.stringWidth(label)) / 2;
            int y = (diameter + fm.getAscent()) / 2 - fm.getDescent();
            g2.setColor(Color.BLACK);
            g2.drawString(label, x, y);
        } finally
        {
            g2.dispose();
        }
    }

    /**
     * Sets the fill color of the vertex and repaints.
     * @param color The new fill color
     */
    public void setColor(Color color)
    {
        this.color = color;
        repaint();
    }

    /**
     * Sets the id of the vertex and repaints.
     * @param id The new identifier
     */
    public void setId(int id)
    {
        this.id = id;
        repaint();
    }

    /**
     * Returns the diameter of the vertex.
     * @return The diameter in pixels
     */
    public int getDiameter()
    {
        return diameter;
    }
}
