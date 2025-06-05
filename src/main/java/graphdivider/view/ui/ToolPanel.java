package graphdivider.view.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

// Panel with controls for partitioning a graph
public final class ToolPanel extends JPanel
{
    // Spinner for number of partitions
    private final JSpinner partitionsSpinner;
    // Spinner for margin percentage
    private final JSpinner marginSpinner;
    // Button to divide the graph
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
        partitionsSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 100, 1));
        partitionsSpinner.setEnabled(false);
        add(partitionsSpinner, gbc);

        // Row 1: Margin %
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Margin %:"), gbc);
        gbc.gridx = 1;
        marginSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 999, 1));
        marginSpinner.setEnabled(false);
        add(marginSpinner, gbc);

        // Row 2: Divide Graph button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        divideButton = new JButton("Divide Graph");
        divideButton.setEnabled(false);
        add(divideButton, gbc);
    }

    // Listen for spinner changes
    public void addChangeListener(ChangeListener listener)
    {
        partitionsSpinner.addChangeListener(listener);
        marginSpinner.addChangeListener(listener);
    }

    // Listen for divide button click
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

    // Enable/disable divide button
    public void setDivideButtonEnabled(boolean enabled)
    {
        divideButton.setEnabled(enabled);
    }

    // Set spinner value for partitions
    public void setPartitionsSpinnerValue(int value)
    {
        partitionsSpinner.setValue(value);
    }

    // Set max allowed partitions
    public void setMaxPartitions(int maxPartitions)
    {
        SpinnerNumberModel model = (SpinnerNumberModel) partitionsSpinner.getModel();
        model.setMaximum(maxPartitions);
        partitionsSpinner.setToolTipText("Minimum: 2\nMaximum: " + maxPartitions);
    }

    // Get spinner for partitions
    public JSpinner getPartitionsSpinner()
    {
        return partitionsSpinner;
    }

    // Get spinner for margin
    public JSpinner getMarginSpinner()
    {
        return marginSpinner;
    }
}
