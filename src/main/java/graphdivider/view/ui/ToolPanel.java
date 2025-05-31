package graphdivider.view.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

// ToolPanel provides user controls for partitioning a graph
public final class ToolPanel extends JPanel
{
    // Spinner for selecting the number of partitions (minimum 2, maximum 100)
    private final JSpinner partitionsSpinner;
    // Spinner for selecting the margin percentage (minimum 10, maximum 999)
    private final JSpinner marginSpinner;
    // Button to trigger the graph division operation
    private final JButton divideButton;

    // Constructs the tool panel with controls for partition settings and graph division
    public ToolPanel()
    {
        // Use ToolPanelBuilder to construct the layout and add components
        ToolPanelBuilder builder = new ToolPanelBuilder();
        setBorder(BorderFactory.createTitledBorder("Partition Settings"));
        builder.addLabel("Number of parts:")
               .addSpinner(partitionsSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 100, 1)))
               .addLabel("Margin %:")
               .addSpinner(marginSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 999, 1)))
               .addButton(divideButton = new JButton("Divide Graph"));
        // Disable the divide button until a graph is loaded
        divideButton.setEnabled(false);
        builder.applyTo(this);
    }

    // Registers ChangeListeners for the spinners
    public void addChangeListener(ChangeListener listener)
    {
        partitionsSpinner.addChangeListener(listener);
        marginSpinner.addChangeListener(listener);
    }

    // Registers ActionListener for the divide button
    public void addDivideButtonListener(ActionListener listener)
    {
        divideButton.addActionListener(listener);
    }

    // Gets the currqent number of partitions from the spinner
    public int getPartitions()
    {
        return (Integer) partitionsSpinner.getValue();
    }

    // Gets the current margin percentage from the spinner
    public int getMargin()
    {
        return (Integer) marginSpinner.getValue();
    }

    // Enables or disables the divide button based on graph state
    public void setDivideButtonEnabled(boolean enabled)
    {
        divideButton.setEnabled(enabled);
    }

    // Builder class for constructing the tool panel layout
    private static class ToolPanelBuilder
    {
        private final GridBagLayout layout = new GridBagLayout();
        private final GridBagConstraints gbc = new GridBagConstraints();
        private final JPanel panel = new JPanel();

        // Initializes the builder with a GridBagLayout and default constraints
        ToolPanelBuilder()
        {
            panel.setLayout(layout);
            // Set spacing between components
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.anchor = GridBagConstraints.WEST;
        }

        // Adds a label and spinner to the next row in the panel
        ToolPanelBuilder addLabel(String text)
        {
            gbc.gridx = 0;
            gbc.gridy = panel.getComponentCount() / 2;
            panel.add(new JLabel(text), gbc);
            return this;
        }

        // Adds a spinner component to the next row in the panel
        ToolPanelBuilder addSpinner(JSpinner spinner)
        {
            gbc.gridx = 1;
            gbc.gridy = panel.getComponentCount() / 2;
            panel.add(spinner, gbc);
            return this;
        }

        // Adds a button to the next row in the panel
        void addButton(JButton button)
        {
            gbc.gridx = 0;
            gbc.gridy = panel.getComponentCount() / 2;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(button, gbc);
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
        }

        // Applies the constructed layout and components to a target JPanel
        void applyTo(JPanel target)
        {
            target.setLayout(layout);
            for (Component c : panel.getComponents())
            {
                target.add(c, ((GridBagLayout) panel.getLayout()).getConstraints(c));
            }
        }
    }
}
