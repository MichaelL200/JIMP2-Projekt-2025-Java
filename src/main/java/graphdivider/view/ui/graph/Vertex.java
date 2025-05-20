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
     * Default blue light color for vertices.
     */
    public static final Color DEFAULT_BLUE = new Color(173, 216, 230);

    /**
     * Unique identifier for the vertex.
     */
    private int id;

    /**
     * Fill color of the vertex.
     */
    private Color color;

    /**
     * Diameter of the vertex circle (in pixels).
     */
    private final int diameter;

    /**
     * Constructs a Vertex with the given id, color, and diameter.
     * 
     * @param id the identifier to display inside the vertex
     * @param color the fill color of the vertex
     * @param diameter the diameter (in pixels) of the vertex
     */
    public Vertex(int id, Color color, int diameter)
    {
        this.id = id;
        this.color = color;
        this.diameter = diameter;

        // Set the preferred, minimum, and maximum size to ensure the component is always a circle
        Dimension size = new Dimension(diameter, diameter);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }

    /**
     * Paints the vertex as a filled circle with a centered label.
     * 
     * @param g the Graphics context in which to paint
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // Create a copy of the graphics context for safe drawing
        Graphics2D g2 = (Graphics2D) g.create();

        try
        {
            // Enable anti-aliasing for smooth edges
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the filled circle (no border)
            g2.setColor(color);
            g2.fillOval(0, 0, diameter, diameter);

            // Prepare the label (vertex id) and center it
            String label = String.valueOf(id);
            FontMetrics fm = g2.getFontMetrics();

            // Calculate coordinates to center the label
            int x = (diameter - fm.stringWidth(label)) / 2;
            int y = (diameter + fm.getAscent()) / 2 - fm.getDescent();

            // Always use black for the label
            g2.setColor(Color.BLACK);

            // Draw the label
            g2.drawString(label, x, y);
        }
        finally
        {
            // Always dispose graphics context to free resources
            g2.dispose();
        }
    }

    /**
     * Sets the fill color of the vertex and repaints.
     * 
     * @param color the new fill color
     */
    public void setColor(Color color)
    {
        this.color = color;
        repaint();
    }

    /**
     * Sets the id of the vertex and repaints.
     * 
     * @param id the new identifier
     */
    public void setId(int id)
    {
        this.id = id;
        repaint();
    }

    /**
     * Returns the diameter of the vertex.
     * 
     * @return the diameter in pixels
     */
    public int getDiameter()
    {
        return diameter;
    }
}

