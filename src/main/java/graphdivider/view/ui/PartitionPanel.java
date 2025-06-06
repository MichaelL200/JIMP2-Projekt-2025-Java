package graphdivider.view.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for displaying partitioning results (edges cut, margin kept, etc.)
 */
public class PartitionPanel extends JPanel
{
    // Shows number of edges cut
    private final JLabel edgesCutLabel;
    // Shows margin kept (%)
    private final JLabel marginKeptLabel;

    // Store marginKept value for access
    private double marginKept = 0.0;

    // Setup panel layout and labels
    public PartitionPanel()
    {
        setLayout(new GridLayout(2, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Partition Info"));

        edgesCutLabel = new JLabel("Edges cut: 0");
        marginKeptLabel = new JLabel("Margin kept: 0.00");

        add(edgesCutLabel);
        add(marginKeptLabel);
    }

    // Set edges cut label
    public void setEdgesCut(int edgesCut)
    {
        edgesCutLabel.setText("Edges cut: " + edgesCut);
    }

    // Set labels to unknown state
    public void setUnknown()
    {
        edgesCutLabel.setText("Edges cut: -");
        marginKeptLabel.setText("Margin kept: -");
        this.marginKept = 0.0;
    }

    // Get the stored margin kept value
    public double getMarginKept()
    {
        return marginKept;
    }

    // Set margin kept label
    public void setMarginKept(double marginKept)
    {
        this.marginKept = marginKept;
        marginKeptLabel.setText(String.format("Margin kept: %.2f", marginKept));
    }

    // Clear both labels
    public void clear()
    {
        setEdgesCut(0);
        setMarginKept(0.0);
    }
}