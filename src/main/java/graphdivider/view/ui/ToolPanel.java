package graphdivider.view.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * ToolPanel provides user controls for partitioning a graph.
 * It contains spinners for the number of partitions and margin,
 * and a button to trigger the division operation.
 */
public final class ToolPanel extends JPanel
{
    // Spinner for selecting the number of partitions (minimum 2, maximum 100)
    private final JSpinner partitionsSpinner;
    // Spinner for selecting the margin percentage (minimum 10, maximum 999)
    private final JSpinner marginSpinner;
    // Button to trigger the graph division operation
    private final JButton divideButton;

    /**
     * Constructs the tool panel with controls for partition settings and graph division.
     * Uses a builder pattern for concise and readable UI layout construction.
     */
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

    /**
     * Registers a ChangeListener for both spinners (number of partitions and margin).
     * Allows external components to react to user input changes.
     *
     * @param listener the listener to notify on value changes
     */
    public void addChangeListener(ChangeListener listener)
    {
        partitionsSpinner.addChangeListener(listener);
        marginSpinner.addChangeListener(listener);
    }

    /**
     * Registers an ActionListener for the Divide Graph button.
     * Allows external components to handle the divide action.
     *
     * @param listener the listener to notify on button press
     */
    public void addDivideButtonListener(ActionListener listener)
    {
        divideButton.addActionListener(listener);
    }

    /**
     * Gets the current number of partitions from the spinner.
     *
     * @return the number of partitions selected by the user
     */
    public int getPartitions()
    {
        return (Integer) partitionsSpinner.getValue();
    }

    /**
     * Gets the current margin percentage from the spinner.
     *
     * @return the margin percentage selected by the user
     */
    public int getMargin()
    {
        return (Integer) marginSpinner.getValue();
    }

    /**
     * Enables or disables the divide button.
     *
     * @param enabled true to enable, false to disable
     */
    public void setDivideButtonEnabled(boolean enabled)
    {
        divideButton.setEnabled(enabled);
    }

    /**
     * ToolPanelBuilder helps construct the ToolPanel layout using GridBagLayout.
     * It encapsulates repetitive layout code for clarity and maintainability.
     */
    private static class ToolPanelBuilder
    {
        private final GridBagLayout layout = new GridBagLayout();
        private final GridBagConstraints gbc = new GridBagConstraints();
        private final JPanel panel = new JPanel();

        /**
         * Initializes the builder with default layout and spacing.
         */
        ToolPanelBuilder()
        {
            panel.setLayout(layout);
            // Set spacing between components
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.anchor = GridBagConstraints.WEST;
        }

        /**
         * Adds a label to the next row in the panel.
         *
         * @param text the label text
         * @return this builder for chaining
         */
        ToolPanelBuilder addLabel(String text)
        {
            gbc.gridx = 0;
            gbc.gridy = panel.getComponentCount() / 2;
            panel.add(new JLabel(text), gbc);
            return this;
        }

        /**
         * Adds a spinner to the next row in the panel.
         *
         * @param spinner the spinner component
         * @return this builder for chaining
         */
        ToolPanelBuilder addSpinner(JSpinner spinner)
        {
            gbc.gridx = 1;
            gbc.gridy = panel.getComponentCount() / 2;
            panel.add(spinner, gbc);
            return this;
        }

        /**
         * Adds a button spanning two columns to the next row in the panel.
         *
         * @param button the button component
         * @return this builder for chaining
         */
        ToolPanelBuilder addButton(JButton button)
        {
            gbc.gridx = 0;
            gbc.gridy = panel.getComponentCount() / 2;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(button, gbc);
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            return this;
        }

        /**
         * Applies the built layout and components to the target panel.
         * Copies all components and their constraints to the given JPanel.
         *
         * @param target the panel to apply the layout to
         */
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
