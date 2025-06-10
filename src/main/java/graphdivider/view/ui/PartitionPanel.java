package graphdivider.view.ui;

import graphdivider.view.Language;
import java.util.Locale;
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

    // Update panel texts based on language
    public void updateTexts()
    {
        Locale locale = Language.getCurrentLocale();
        boolean isPolish = locale != null && locale.getLanguage().equals("pl");
        ((javax.swing.border.TitledBorder) getBorder()).setTitle(isPolish ? "Informacje o podziale" : "Partition Info");

        String edgesCutPrefix = isPolish ? "Krawędzie przecięte: " : "Edges cut: ";
        String marginKeptPrefix = isPolish ? "Zachowany margines: " : "Margin kept: ";
        String unknown = "-";

        // Update edges cut label
        String edgesCutText = edgesCutLabel.getText();
        if (edgesCutText.endsWith("-"))
        {
            edgesCutLabel.setText(edgesCutPrefix + unknown);
        } else
        {
            String value = edgesCutText.replaceAll(".*?:\\s*", "");
            edgesCutLabel.setText(edgesCutPrefix + value);
        }

        // Update margin kept label
        String marginKeptText = marginKeptLabel.getText();
        if (marginKeptText.endsWith("-"))
        {
            marginKeptLabel.setText(marginKeptPrefix + unknown);
        } else
        {
            String value = marginKeptText.replaceAll(".*?:\\s*", "");
            marginKeptLabel.setText(marginKeptPrefix + value);
        }
        repaint();
    }
}