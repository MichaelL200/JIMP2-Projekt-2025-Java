package graphdivider.view.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

// ToolPanel provides user controls for partitioning a graph.
public final class ToolPanel extends JPanel
{
    // Spinner for selecting the number of partitions (minimum 2, maximum 100)
    private final JSpinner partitionsSpinner;
    // Spinner for selecting the margin percentage (minimum 10, maximum 999)
    private final JSpinner marginSpinner;
    // Button to trigger the graph division operation
    private final JButton divideButton;

    // Constructs the tool panel with controls for partition settings and graph division.
    public ToolPanel()
    {
        // Use GridBagLayout directly for clarity and efficiency
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
        add(partitionsSpinner, gbc);

        // Row 1: Margin %
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Margin %:"), gbc);
        gbc.gridx = 1;
        marginSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 999, 1));
        add(marginSpinner, gbc);

        // Row 2: Divide Graph button (span 2 columns)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        divideButton = new JButton("Divide Graph");
        divideButton.setEnabled(false);
        add(divideButton, gbc);
    }

    // Registers a ChangeListener for both spinners.
    public void addChangeListener(ChangeListener listener)
    {
        partitionsSpinner.addChangeListener(listener);
        marginSpinner.addChangeListener(listener);
    }

    // Registers an ActionListener for the Divide Graph button.
    public void addDivideButtonListener(ActionListener listener)
    {
        divideButton.addActionListener(listener);
    }

    // Gets the current number of partitions from the spinner.
    public int getPartitions()
    {
        return (Integer) partitionsSpinner.getValue();
    }

    // Gets the current margin percentage from the spinner.
    public int getMargin()
    {
        return (Integer) marginSpinner.getValue();
    }

    // Enables or disables the divide button.
    public void setDivideButtonEnabled(boolean enabled)
    {
        divideButton.setEnabled(enabled);
    }
}
