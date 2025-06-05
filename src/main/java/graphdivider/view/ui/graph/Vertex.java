package graphdivider.view.ui.graph;

import javax.swing.*;
import java.awt.*;

// One vertex (node) in the graph view
public final class Vertex extends JComponent
{
    // Default color for vertices
    public static final Color DEFAULT_BLUE = new Color(173, 216, 230);
    // Diameter of the vertex circle
    private final int diameter;
    // Vertex id (label)
    private int id;
    // Current color
    private Color color;

    // Create vertex with id, color, and size
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

    // Draw the vertex as a circle with label
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

            // Draw id label in center
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

    // Change color and repaint
    public void setColor(Color color)
    {
        this.color = color;
        repaint();
    }

    // Change id and repaint
    public void setId(int id)
    {
        this.id = id;
        repaint();
    }

    // Get id
    public int getId()
    {
        return this.id;
    }

    // Get diameter
    public int getDiameter()
    {
        return diameter;
    }
}
