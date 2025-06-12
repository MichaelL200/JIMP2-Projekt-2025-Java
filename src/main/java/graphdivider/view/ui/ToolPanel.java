package graphdivider.view.ui;

import graphdivider.view.Language;
import java.util.Locale;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel with controls for partitioning a graph.
 * Provides spinners for partition count and margin, and a button to trigger partitioning.
 */
public final class ToolPanel extends JPanel
{
    // Spinner for selecting number of partitions
    private final JSpinner partitionCountSpinner;
    // Spinner for selecting margin percentage
    private final JSpinner partitionMarginSpinner;
    // Button to trigger graph partitioning
    private final JButton partitionButton;
    // Label for partition count spinner
    private final JLabel partitionCountLabel;
    // Label for margin spinner
    private final JLabel partitionMarginLabel;

    /**
     * Constructs the ToolPanel and initializes all UI components.
     * Sets up layout, labels, spinners, and button.
     */
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
        gbc.gridx = 0; 
        gbc.gridy = 0;
        partitionCountLabel = new JLabel("Number of parts:");
        add(partitionCountLabel, gbc);
        gbc.gridx = 1;
        partitionCountSpinner = createSpinner(2, 2, 100, 1, false);
        add(partitionCountSpinner, gbc);

        // Row 1: Margin %
        gbc.gridx = 0; 
        gbc.gridy = 1;
        partitionMarginLabel = new JLabel("Margin %:");
        add(partitionMarginLabel, gbc);
        gbc.gridx = 1;
        partitionMarginSpinner = createSpinner(10, 10, 999, 1, false);
        add(partitionMarginSpinner, gbc);

        // Row 2: Partition Graph button
        gbc.gridx = 0; 
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        partitionButton = new JButton("Divide Graph");
        partitionButton.setEnabled(false);
        add(partitionButton, gbc);
    }

    /**
     * Returns a new instance of ToolPanel.
     * 
     * @return a new ToolPanel object.
     */
    public static Object getInstance()
    {
        return new ToolPanel();
    }

    /**
     * Helper to create a spinner with given parameters.
     * 
     * @param value Initial value.
     * @param min Minimum value.
     * @param max Maximum value.
     * @param step Step size.
     * @param enabled Whether the spinner is enabled.
     * @return Configured JSpinner.
     */
    private JSpinner createSpinner(int value, int min, int max, int step, boolean enabled)
    {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        spinner.setEnabled(enabled);
        return spinner;
    }

    /**
     * Adds a ChangeListener to both spinners.
     * 
     * @param listener Listener to add.
     */
    public void addSpinnerChangeListener(ChangeListener listener)
    {
        partitionCountSpinner.addChangeListener(listener);
        partitionMarginSpinner.addChangeListener(listener);
    }

    /**
     * Adds an ActionListener to the partition button.
     * 
     * @param listener Listener to add.
     */
    public void addPartitionButtonActionListener(ActionListener listener)
    {
        partitionButton.addActionListener(listener);
    }

    /**
     * Gets the current number of partitions from the spinner.
     * 
     * @return Number of partitions.
     */
    public int getPartitionCount()
    {
        return (Integer) partitionCountSpinner.getValue();
    }

    /**
     * Gets the current margin percent from the spinner.
     * 
     * @return Margin percent.
     */
    public int getPartitionMargin()
    {
        return (Integer) partitionMarginSpinner.getValue();
    }

    /**
     * Enables or disables the partition button.
     * 
     * @param enabled True to enable, false to disable.
     */
    public void setPartitionButtonEnabled(boolean enabled)
    {
        partitionButton.setEnabled(enabled);
    }

    /**
     * Sets the value for the partition count spinner.
     * 
     * @param value Value to set.
     */
    public void setPartitionCountSpinnerValue(int value)
    {
        partitionCountSpinner.setValue(value);
    }

    /**
     * Sets the maximum allowed partitions and updates tooltip.
     * 
     * @param maxPartitions Maximum number of partitions.
     */
    public void setMaxPartitionCount(int maxPartitions)
    {
        SpinnerNumberModel model = (SpinnerNumberModel) partitionCountSpinner.getModel();
        model.setMaximum(maxPartitions);
        partitionCountSpinner.setToolTipText("Minimum: 2\nMaximum: " + maxPartitions);
    }

    /**
     * Sets the minimum allowed margin and updates tooltip.
     * 
     * @param minMargin Minimum margin percent.
     */
    public void setMinPartitionMargin(int minMargin)
    {
        SpinnerNumberModel model = (SpinnerNumberModel) partitionMarginSpinner.getModel();
        model.setMinimum(minMargin);
        partitionMarginSpinner.setToolTipText("Minimum: " + minMargin + "%");
    }

    /**
     * Gets the spinner for partition count (for external control).
     * 
     * @return JSpinner for partition count.
     */
    public JSpinner getPartitionCountSpinner()
    {
        return partitionCountSpinner;
    }

    /**
     * Gets the spinner for margin (for external control).
     * 
     * @return JSpinner for margin percent.
     */
    public JSpinner getPartitionMarginSpinner()
    {
        return partitionMarginSpinner;
    }

    /**
     * Updates panel texts based on the current language.
     * Uses Polish if set, otherwise English.
     */
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
