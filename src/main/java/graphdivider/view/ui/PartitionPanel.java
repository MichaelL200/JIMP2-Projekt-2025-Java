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

    // Aktualizuj teksty panelu na podstawie języka
    public void updateTexts() {
        Locale locale = Language.getCurrentLocale();
        boolean isPolish = locale != null && locale.getLanguage().equals("pl");
        ((javax.swing.border.TitledBorder) getBorder()).setTitle(isPolish ? "Informacje o podziale" : "Partition Info");
        // Zmień teksty etykiet zgodnie z aktualnym językiem i stanem
        String edgesCutText = isPolish ? "Krawędzie przecięte: " : "Edges cut: ";
        String marginKeptText = isPolish ? "Zachowany margines: " : "Margin kept: ";
        String unknown = isPolish ? "-" : "-";
        // Rozpoznaj aktualny stan etykiet i zaktualizuj je
        if (edgesCutLabel.getText().matches(".*-.*")) {
            edgesCutLabel.setText(edgesCutText + unknown);
        } else if (edgesCutLabel.getText().matches(".*\\d+.*")) {
            String value = edgesCutLabel.getText().replaceAll("[^0-9]", "");
            edgesCutLabel.setText(edgesCutText + value);
        }
        if (marginKeptLabel.getText().matches(".*-.*")) {
            marginKeptLabel.setText(marginKeptText + unknown);
        } else if (marginKeptLabel.getText().matches(".*\\d.*")) {
            String value = marginKeptLabel.getText().replaceAll("[^0-9.,]", "");
            marginKeptLabel.setText(marginKeptText + value);
        }
        repaint();
    }
}