package graphdivider.view.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

// Panel with controls for partitioning a graph
public final class ToolPanel extends JPanel
{
    // Spinner: number of partitions
    private final JSpinner partitionsSpinner;
    // Spinner: margin percentage
    private final JSpinner marginSpinner;
    // Button: divide the graph
    private final JButton divideButton;

    public ToolPanel()
    {
        setBorder(BorderFactory.createTitledBorder("Partition Settings"));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Number of parts
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Number of parts:"), gbc);
        gbc.gridx = 1;
        partitionsSpinner = createSpinner(2, 2, 100, 1, false);
        add(partitionsSpinner, gbc);

        // Row 1: Margin %
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Margin %:"), gbc);
        gbc.gridx = 1;
        marginSpinner = createSpinner(10, 10, 999, 1, false);
        add(marginSpinner, gbc);

        // Row 2: Divide Graph button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        divideButton = new JButton("Divide Graph");
        divideButton.setEnabled(false);
        add(divideButton, gbc);
    }

    // Helper: create a spinner with given params
    private JSpinner createSpinner(int value, int min, int max, int step, boolean enabled) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        spinner.setEnabled(enabled);
        return spinner;
    }

    // Add listener for both spinners
    public void addChangeListener(ChangeListener listener)
    {
        partitionsSpinner.addChangeListener(listener);
        marginSpinner.addChangeListener(listener);
    }

    // Add listener for divide button
    public void addDivideButtonActionListener(ActionListener listener)
    {
        divideButton.addActionListener(listener);
    }

    // Get number of partitions
    public int getPartitions()
    {
        return (Integer) partitionsSpinner.getValue();
    }

    // Get margin percent
    public int getMargin()
    {
        return (Integer) marginSpinner.getValue();
    }

    // Enable or disable divide button
    public void setDivideButtonEnabled(boolean enabled)
    {
        divideButton.setEnabled(enabled);
    }

    // Set value for partitions spinner
    public void setPartitionsSpinnerValue(int value)
    {
        partitionsSpinner.setValue(value);
    }

    // Set max allowed partitions and update tooltip
    public void setMaxPartitions(int maxPartitions)
    {
        SpinnerNumberModel model = (SpinnerNumberModel) partitionsSpinner.getModel();
        model.setMaximum(maxPartitions);
        partitionsSpinner.setToolTipText("Minimum: 2\nMaximum: " + maxPartitions);
    }

    // Set min allowed margin and update tooltip
    public void setMinMargin(int minMargin)
    {
        SpinnerNumberModel model = (SpinnerNumberModel) marginSpinner.getModel();
        model.setMinimum(minMargin);
        marginSpinner.setToolTipText("Minimum: " + minMargin + "%");
    }

    // Get spinner for partitions (for external control)
    public JSpinner getPartitionsSpinner()
    {
        return partitionsSpinner;
    }

    // Get spinner for margin (for external control)
    public JSpinner getMarginSpinner()
    {
        return marginSpinner;
    }
}
