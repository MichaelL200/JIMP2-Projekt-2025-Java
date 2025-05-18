package graphdivider.view.ui.graph;

import graphdivider.view.ui.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Component representing a single graph vertex as a colored circle with a label.
 */
public class Vertex extends JComponent
{
    private int id;
    private Color color;
    private int diameter;

    /**
     * Creates a vertex component.
     *
     * @param id       numeric identifier to display in the center
     * @param color    fill color of the circle
     * @param diameter diameter in pixels of the circle
     */
    public Vertex(int id, Color color, int diameter)
    {
        this.id = id;
        this.color = color;
        this.diameter = diameter;
        // set preferred size so layout managers know how big this component wants to be
        setPreferredSize(new Dimension(diameter, diameter));
        setMinimumSize(new Dimension(diameter, diameter));
        setMaximumSize(new Dimension(diameter, diameter));
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try
        {
            // enable anti-aliasing
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // draw filled circle
            g2.setColor(color);
            g2.fillOval(0, 0, diameter, diameter);

            // draw border
            g2.setColor(color.darker());
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(0, 0, diameter, diameter);

            // draw id text centered
            String label = String.valueOf(id);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getAscent();
            int x = (diameter - textWidth) / 2;
            int y = (diameter + textHeight) / 2 - fm.getDescent();

            // choose text color based on theme
            boolean dark = Theme.isDarkPreferred();
            g2.setColor(dark ? Color.WHITE : Color.BLACK);
            g2.drawString(label, x, y);
        }
        finally
        {
            g2.dispose();
        }
    }

    /**
     * Update the vertex's color.
     */
    public void setColor(Color color)
    {
        this.color = color;
        repaint();
    }

    /**
     * Update the vertex's id label.
     */
    public void setId(int id)
    {
        this.id = id;
        repaint();
    }

    /**
     * Returns the diameter of this vertex.
     */
    public int getDiameter()
    {
        return diameter;
    }
}
