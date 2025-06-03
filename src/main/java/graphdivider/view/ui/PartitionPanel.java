package graphdivider.view.ui;

import javax.swing.*;
import java.awt.*;

public class PartitionPanel extends JPanel
{
    private final JLabel edgesCutLabel;
    private final JLabel marginKeptLabel;

    public PartitionPanel()
    {
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Partition Data"),
                BorderFactory.createEmptyBorder(60, 20, 60, 20) // top, left, bottom, right
        ));
        setLayout(new GridLayout(2, 1, 5, 15)); // more vertical gap

        edgesCutLabel = new JLabel("Edges cut: -");
        marginKeptLabel = new JLabel("Margin kept: -");

        add(edgesCutLabel);
        add(marginKeptLabel);
    }

    public void setEdgesCut(int edgesCut)
    {
        edgesCutLabel.setText("Edges cut: " + edgesCut);
    }

    public void setMarginKept(double marginKept)
    {
        marginKeptLabel.setText("Margin kept: " + String.format("%.2f%%", marginKept));
    }

    public void clear()
    {
        edgesCutLabel.setText("Edges cut: -");
        marginKeptLabel.setText("Margin kept: -");
    }
}