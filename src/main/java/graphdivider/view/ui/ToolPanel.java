package graphdivider.view.ui;

import graphdivider.view.Language;
import java.util.Locale;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

// Panel with controls for partitioning a graph
public final class ToolPanel extends JPanel
{
    // Spinner: number of partitions
    private final JSpinner partitionCountSpinner;
    // Spinner: margin percentage
    private final JSpinner partitionMarginSpinner;
    // Button: divide the graph
    private final JButton partitionButton;
    private final JLabel partitionCountLabel;
    private final JLabel partitionMarginLabel;

    public ToolPanel()
    {
        setBorder(BorderFactory.createTitledBorder("Partition Settings"));
        setLayout(new GridBagLayout());

        // Set fixed panel width (e.g. 220px), height is automatic
        int fixedWidth = 220;
        setPreferredSize(new Dimension(fixedWidth, getPreferredSize().height));
        setMinimumSize(new Dimension(fixedWidth, 0));
        setMaximumSize(new Dimension(fixedWidth, Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Number of partitions
        gbc.gridx = 0; gbc.gridy = 0;
        partitionCountLabel = new JLabel("Number of parts:");
        add(partitionCountLabel, gbc);
        gbc.gridx = 1;
        partitionCountSpinner = createSpinner(2, 2, 100, 1, false);
        add(partitionCountSpinner, gbc);

        // Row 1: Margin %
        gbc.gridx = 0; gbc.gridy = 1;
        partitionMarginLabel = new JLabel("Margin %:");
        add(partitionMarginLabel, gbc);
        gbc.gridx = 1;
        partitionMarginSpinner = createSpinner(10, 10, 999, 1, false);
        add(partitionMarginSpinner, gbc);

        // Row 2: Partition Graph button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        partitionButton = new JButton("Divide Graph");
        partitionButton.setEnabled(false);
        add(partitionButton, gbc);
    }

    public static Object getInstance()
    {
        return new ToolPanel();
    }

    // Helper: create a spinner with given params
    private JSpinner createSpinner(int value, int min, int max, int step, boolean enabled)
    {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        spinner.setEnabled(enabled);
        return spinner;
    }

    // Add listener for both spinners
    public void addSpinnerChangeListener(ChangeListener listener)
    {
        partitionCountSpinner.addChangeListener(listener);
        partitionMarginSpinner.addChangeListener(listener);
    }

    // Add listener for partition button
    public void addPartitionButtonActionListener(ActionListener listener)
    {
        partitionButton.addActionListener(listener);
    }

    // Get number of partitions
    public int getPartitionCount()
    {
        return (Integer) partitionCountSpinner.getValue();
    }

    // Get margin percent
    public int getPartitionMargin()
    {
        return (Integer) partitionMarginSpinner.getValue();
    }

    // Enable or disable partition button
    public void setPartitionButtonEnabled(boolean enabled)
    {
        partitionButton.setEnabled(enabled);
    }

    // Set value for partition count spinner
    public void setPartitionCountSpinnerValue(int value)
    {
        partitionCountSpinner.setValue(value);
    }

    // Set max allowed partitions and update tooltip
    public void setMaxPartitionCount(int maxPartitions)
    {
        SpinnerNumberModel model = (SpinnerNumberModel) partitionCountSpinner.getModel();
        model.setMaximum(maxPartitions);
        partitionCountSpinner.setToolTipText("Minimum: 2\nMaximum: " + maxPartitions);
    }

    // Set min allowed margin and update tooltip
    public void setMinPartitionMargin(int minMargin)
    {
        SpinnerNumberModel model = (SpinnerNumberModel) partitionMarginSpinner.getModel();
        model.setMinimum(minMargin);
        partitionMarginSpinner.setToolTipText("Minimum: " + minMargin + "%");
    }

    // Get spinner for partition count (for external control)
    public JSpinner getPartitionCountSpinner()
    {
        return partitionCountSpinner;
    }

    // Get spinner for margin (for external control)
    public JSpinner getPartitionMarginSpinner()
    {
        return partitionMarginSpinner;
    }

    // Update panel texts based on language
    public void updateTexts()
    {
        Locale locale = Language.getCurrentLocale();
        boolean isPolish = locale != null && locale.getLanguage().equals("pl");
        ((javax.swing.border.TitledBorder) getBorder()).setTitle(isPolish ? "Ustawienia podziału" : "Partition Settings");
        partitionCountLabel.setText(isPolish ? "Liczba części:" : "Number of parts:");
        partitionMarginLabel.setText(isPolish ? "Margines %:" : "Margin %:");
        partitionButton.setText(isPolish ? "Podziel graf" : "Divide Graph");
        repaint();
    }
}
