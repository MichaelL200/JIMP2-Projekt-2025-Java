package graphdivider.view.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

public final class ToolPanel extends JPanel
{
    private final JSpinner partitionsSpinner;
    private final JSpinner marginSpinner;
    private final JButton divideButton;

    public ToolPanel()
    {
        ToolPanelBuilder builder = new ToolPanelBuilder();
        setBorder(BorderFactory.createTitledBorder("Partition Settings"));
        builder.addLabel("Number of parts:")
               .addSpinner(partitionsSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 100, 1)))
               .addLabel("Margin %:")
               .addSpinner(marginSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 999, 1)))
               .addButton(divideButton = new JButton("Divide Graph"));
        divideButton.setEnabled(false); // Button is initially disabled until valid input is provided
        builder.applyTo(this);
    }

    /**
     * Registers a ChangeListener for both spinners.
     * @param listener the listener to notify on value changes
     */
    public void addChangeListener(ChangeListener listener)
    {
        partitionsSpinner.addChangeListener(listener);
        marginSpinner.addChangeListener(listener);
    }

    /**
     * Registers an ActionListener for the Divide Graph button.
     * @param listener the listener to notify on button press
     */
    public void addDivideButtonListener(ActionListener listener)
    {
        divideButton.addActionListener(listener);
    }

    /**
     * Gets the current number of partitions from the spinner.
     * @return the number of partitions
     */
    public int getPartitions()
    {
        return (Integer) partitionsSpinner.getValue();
    }

    /**
     * Gets the current margin percentage from the spinner.
     * @return the margin percentage
     */
    public int getMargin()
    {
        return (Integer) marginSpinner.getValue();
    }

    /**
     * Builder class for constructing the tool panel layout with spacing.
     */
    private static class ToolPanelBuilder
    {
        private final GridBagLayout layout = new GridBagLayout();
        private final GridBagConstraints gbc = new GridBagConstraints();
        private final JPanel panel = new JPanel();

        ToolPanelBuilder()
        {
            panel.setLayout(layout);
            // Add vertical and horizontal insets for spacing between options
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.anchor = GridBagConstraints.WEST;
        }

        ToolPanelBuilder addLabel(String text)
        {
            gbc.gridx = 0;
            gbc.gridy = panel.getComponentCount() / 2;
            panel.add(new JLabel(text), gbc);
            return this;
        }

        ToolPanelBuilder addSpinner(JSpinner spinner)
        {
            gbc.gridx = 1;
            gbc.gridy = panel.getComponentCount() / 2;
            panel.add(spinner, gbc);
            return this;
        }

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
         * Applies the built layout to the target panel.
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
