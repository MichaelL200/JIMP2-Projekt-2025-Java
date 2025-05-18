package graphdivider.view.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Panel with controls to define number of partitions and percentage margin.
 */
public class ToolPanel extends JPanel
{
    private final JSpinner partitionsSpinner;
    private final JSpinner marginSpinner;

    public ToolPanel()
    {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Partition Settings"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Number of partitions label
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Number of parts:"), gbc);

        // Number of partitions spinner
        partitionsSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 100, 1));
        gbc.gridx = 1;
        add(partitionsSpinner, gbc);

        // Margin percentage label
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Margin %:"), gbc);

        // Margin percentage spinner
        marginSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 999, 1));
        gbc.gridx = 1;
        add(marginSpinner, gbc);
    }

    /**
     * Register listener for changes in both controls
     */
    public void addChangeListener(ChangeListener listener)
    {
        partitionsSpinner.addChangeListener(listener);
        marginSpinner.addChangeListener(listener);
    }

    /**
     * Get current number of partitions
     */
    public int getPartitions()
    {
        return (Integer) partitionsSpinner.getValue();
    }

    /**
     * Get current margin percentage
     */
    public int getMargin()
    {
        return (Integer) marginSpinner.getValue();
    }
}
