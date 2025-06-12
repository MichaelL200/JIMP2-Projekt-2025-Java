package graphdivider.view.ui;

import graphdivider.view.Language;
import java.util.Locale;
import javax.swing.*;
import java.awt.*;

/**
 * Panel for displaying partitioning results (edges cut, margin kept, etc.)
 * Shows summary information after partitioning a graph.
 */
public class PartitionPanel extends JPanel
{
    // Label showing number of edges cut
    private final JLabel edgesCutLabel;
    // Label showing margin kept (%)
    private final JLabel marginKeptLabel;

    // Stores the marginKept value for access
    private double marginKept = 0.0;

    /**
     * Constructs the PartitionPanel and initializes layout and labels.
     */
    public PartitionPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder
        (
            BorderFactory.createTitledBorder
            (
                null, "Partition Info", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP
            ),
            BorderFactory.createEmptyBorder(30, 0, 30, 0)
        ));

        add(Box.createVerticalGlue());
        edgesCutLabel = new JLabel("Edges cut: 0", SwingConstants.CENTER);
        marginKeptLabel = new JLabel("Margin kept: 0.00", SwingConstants.CENTER);
        edgesCutLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        marginKeptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(edgesCutLabel);
        add(Box.createVerticalStrut(10));
        add(marginKeptLabel);
        add(Box.createVerticalGlue());
    }

    /**
     * Sets the label for number of edges cut.
     * 
     * @param edgesCut Number of edges cut by the partition.
     */
    public void setEdgesCut(int edgesCut)
    {
        edgesCutLabel.setText("Edges cut: " + edgesCut);
    }

    /**
     * Sets both labels to an unknown state (e.g., before partitioning).
     * Resets marginKept to 0.0.
     */
    public void setUnknown()
    {
        edgesCutLabel.setText("Edges cut: -");
        marginKeptLabel.setText("Margin kept: -");
        this.marginKept = 0.0;
    }

    /**
     * Gets the stored margin kept value.
     * 
     * @return Margin kept as a double.
     */
    public double getMarginKept()
    {
        return marginKept;
    }

    /**
     * Sets the label for margin kept and stores the value.
     * 
     * @param marginKept Margin kept as a percentage.
     */
    public void setMarginKept(double marginKept)
    {
        this.marginKept = marginKept;
        marginKeptLabel.setText(String.format("Margin kept: %.2f%%", marginKept));
    }

    /**
     * Clears both labels (sets edges cut to 0 and margin kept to 0.0).
     */
    public void clear()
    {
        setEdgesCut(0);
        setMarginKept(0.0);
    }

    /**
     * Updates panel texts and labels based on the current language.
     * Adjusts border title and label prefixes for Polish or English.
     */
    public void updateTexts()
    {
        Locale locale = Language.getCurrentLocale();
        boolean isPolish = locale != null && locale.getLanguage().equals("pl");
        // Center the titled border text
        javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder) getBorder();
        border.setTitle(isPolish ? "Informacje o podziale" : "Partition Info");
        border.setTitleJustification(javax.swing.border.TitledBorder.CENTER);

        String edgesCutPrefix = isPolish ? "Krawędzie przecięte: " : "Edges cut: ";
        String marginKeptPrefix = isPolish ? "Zachowany margines: " : "Margin kept: ";
        String unknown = "-";

        // Update edges cut label
        String edgesCutText = edgesCutLabel.getText();
        String edgesCutValue = edgesCutText.endsWith("-")
                ? unknown
                : edgesCutText.replaceAll(".*?:\\s*", "");
        edgesCutLabel.setText(edgesCutPrefix + edgesCutValue);

        // Update margin kept label
        String marginKeptText = marginKeptLabel.getText();
        String marginKeptValue = marginKeptText.endsWith("-")
                ? unknown
                : marginKeptText.replaceAll(".*?:\\s*", "");
        if (!marginKeptValue.endsWith("%") && !marginKeptValue.equals(unknown)) {
            marginKeptValue += "%";
        }
        marginKeptLabel.setText(marginKeptPrefix + marginKeptValue);

        repaint();
    }
}