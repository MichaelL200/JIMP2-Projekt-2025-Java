package graphdivider.view.ui.graph;

import graphdivider.view.ui.Theme;

import javax.swing.*;
import java.awt.*;

public class Vertex extends JComponent
{
    private int id;
    private Color color;
    private final int diameter;

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

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(color);
        g2.fillOval(0, 0, diameter, diameter);

        g2.setColor(color.darker());
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(0, 0, diameter, diameter);

        String label = String.valueOf(id);
        FontMetrics fm = g2.getFontMetrics();
        int x = (diameter - fm.stringWidth(label)) / 2;
        int y = (diameter + fm.getAscent()) / 2 - fm.getDescent();
        g2.setColor(Theme.isDarkPreferred() ? Color.WHITE : Color.BLACK);
        g2.drawString(label, x, y);

        g2.dispose();
    }

    public void setColor(Color color)
    {
        this.color = color;
        repaint();
    }

    public void setId(int id)
    {
        this.id = id;
        repaint();
    }

    public int getDiameter()
    {
        return diameter;
    }
}
