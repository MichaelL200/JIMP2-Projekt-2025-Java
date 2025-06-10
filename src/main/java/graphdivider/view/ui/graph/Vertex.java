package graphdivider.view.ui.graph;

import graphdivider.view.Language;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

// Represents a vertex (node) in the graph view
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
    // Neighbors (connected vertices)
    private List<Vertex> neighbors;

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

    // Add tooltip with number of the vertex connections
    public void updateTooltip()
    {
        // Use current language for tooltip
        java.util.Locale locale = Language.getCurrentLocale();
        boolean isPolish = locale != null && locale.getLanguage().equals("pl");

        if (neighbors == null || neighbors.isEmpty())
        {
            setToolTipText(isPolish ? "Brak połączeń" : "No connections");
        } else
        {
            String connected = neighbors.stream()
                    .map(v -> String.valueOf(v.getId()))
                    .collect(Collectors.joining(", "));
            setToolTipText(
                (isPolish ? "Połączony z " : "Connected to ") + connected
            );
        }
    }

    // Add this method to Vertex
    public void setNeighbors(List<Vertex> neighbors)
    {
        this.neighbors = neighbors;
        updateTooltip();
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
