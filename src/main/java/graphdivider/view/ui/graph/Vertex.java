package graphdivider.view.ui.graph;

import graphdivider.view.Language;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a vertex (node) in the graph view.
 * Handles drawing, color, label, and tooltip for the vertex.
 */
public final class Vertex extends JComponent
{
    // Default color for vertices
    public static final Color DEFAULT_BLUE = new Color(173, 216, 230);
    // Diameter of the vertex circle
    private final int diameter;
    // Vertex id (label)
    private int id;
    // Current color of the vertex
    private Color color;
    // List of neighboring vertices (for tooltip)
    private List<Vertex> neighbors;

    /**
     * Creates a vertex with the given id, color, and diameter.
     *
     * @param id Vertex identifier (label).
     * @param color Initial color of the vertex.
     * @param diameter Diameter of the vertex circle in pixels.
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
     * Draws the vertex as a filled circle with its id label centered.
     *
     * @param g Graphics context.
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

            // Draw id label in center
            String label = String.valueOf(id);
            FontMetrics fm = g2.getFontMetrics();
            int x = (diameter - fm.stringWidth(label)) / 2;
            int y = (diameter + fm.getAscent()) / 2 - fm.getDescent();
            g2.setColor(Color.BLACK);
            g2.drawString(label, x, y);
        } 
        finally
        {
            g2.dispose();
        }
    }

    /**
     * Updates the tooltip to show the number of connections or neighbor ids.
     * Uses the current language for tooltip text.
     */
    public void updateTooltip()
    {
        // Use current language for tooltip
        java.util.Locale locale = Language.getCurrentLocale();
        boolean isPolish = locale != null && locale.getLanguage().equals("pl");

        if (neighbors == null || neighbors.isEmpty())
        {
            setToolTipText(isPolish ? "Brak połączeń" : "No connections");
        } 
        else
        {
            String connected = neighbors.stream()
                    .map(v -> String.valueOf(v.getId()))
                    .collect(Collectors.joining(", "));
            setToolTipText(
                (isPolish ? "Połączony z " : "Connected to ") + connected
            );
        }
    }

    /**
     * Sets the neighbors of this vertex and updates the tooltip.
     *
     * @param neighbors List of neighboring Vertex objects.
     */
    public void setNeighborsAndUpdateTooltip(List<Vertex> neighbors)
    {
        this.neighbors = neighbors;
        updateTooltip();
    }

    /**
     * Changes the color of the vertex and repaints it.
     *
     * @param color New color to set.
     */
    public void setColor(Color color)
    {
        this.color = color;
        repaint();
    }

    /**
     * Changes the id of the vertex and repaints it.
     *
     * @param id New id to set.
     */
    public void setId(int id)
    {
        this.id = id;
        repaint();
    }

    /**
     * Gets the id of the vertex.
     *
     * @return Vertex id.
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Gets the diameter of the vertex (for layout).
     *
     * @return Diameter in pixels.
     */
    public int getDiameter()
    {
        return diameter;
    }
}
