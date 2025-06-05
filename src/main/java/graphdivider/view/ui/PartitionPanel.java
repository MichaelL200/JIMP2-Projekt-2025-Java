package graphdivider.view.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for showing partition results (edges cut, margin kept)
 */
public final class PartitionPanel extends JPanel
{
    // Shows number of edges cut
    private final JLabel edgesCutLabel;
    // Shows margin kept (%)
    private final JLabel marginKeptLabel;

    // Setup panel layout and labels
    public PartitionPanel()
    {
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Partition Data"),
                BorderFactory.createEmptyBorder(60, 20, 60, 20)
        ));
        setLayout(new GridLayout(2, 1, 5, 15));

        edgesCutLabel = new JLabel("Edges cut: -");
        marginKeptLabel = new JLabel("Margin kept: -");

        add(edgesCutLabel);
        add(marginKeptLabel);
    }

    // Set edges cut label
    public void setEdgesCut(int edgesCut)
    {
        edgesCutLabel.setText("Edges cut: " + edgesCut);
    }

    // Set margin kept label
    public void setMarginKept(double marginKept)
    {
        marginKeptLabel.setText("Margin kept: " + String.format("%.2f%%", marginKept));
    }

    // Clear both labels
    public void clear()
    {
        edgesCutLabel.setText("Edges cut: -");
        marginKeptLabel.setText("Margin kept: -");
    }
}