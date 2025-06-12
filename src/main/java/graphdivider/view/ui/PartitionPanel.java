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
        marginKeptLabel.setText(String.format("Margin kept: %.2f%%", marginKept));
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