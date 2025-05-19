package graphdivider.view.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Panel with controls to define number of partitions and percentage margin.
 * Uses the Builder pattern for construction.
 */
public class ToolPanel extends JPanel
{
    private final JSpinner partitionsSpinner;
    private final JSpinner marginSpinner;

    public ToolPanel()
    {
        ToolPanelBuilder builder = new ToolPanelBuilder();
        builder.addLabel("Number of parts:", 0, 0)
               .addSpinner(partitionsSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 100, 1)), 1, 0)
               .addLabel("Margin %:", 0, 1)
               .addSpinner(marginSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 999, 1)), 1, 1);
        builder.applyTo(this);
        setBorder(BorderFactory.createTitledBorder("Partition Settings"));
    }

    public void addChangeListener(ChangeListener listener)
    {
        partitionsSpinner.addChangeListener(listener);
        marginSpinner.addChangeListener(listener);
    }

    public int getPartitions()
    {
        return (Integer) partitionsSpinner.getValue();
    }

    public int getMargin()
    {
        return (Integer) marginSpinner.getValue();
    }

    /**
     * Builder for ToolPanel layout.
     */
    private static class ToolPanelBuilder {
        private final GridBagLayout layout = new GridBagLayout();
        private final GridBagConstraints gbc = new GridBagConstraints();
        private final JPanel panel = new JPanel();

        ToolPanelBuilder() {
            panel.setLayout(layout);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
        }

        ToolPanelBuilder addLabel(String text, int x, int y) {
            gbc.gridx = x;
            gbc.gridy = y;
            panel.add(new JLabel(text), gbc);
            return this;
        }

        ToolPanelBuilder addSpinner(JSpinner spinner, int x, int y) {
            gbc.gridx = x;
            gbc.gridy = y;
            panel.add(spinner, gbc);
            return this;
        }

        void applyTo(JPanel target) {
            target.setLayout(layout);
            for (Component c : panel.getComponents()) {
                target.add(c, ((GridBagLayout) panel.getLayout()).getConstraints(c));
            }
        }
    }
}
