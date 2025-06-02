package graphdivider.view.ui;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JDialog
{
    private final JProgressBar progressBar;

    public ProgressDialog(Window parent, String title, String message)
    {
        // Call the superclass constructor to create a non-modal dialog
        super(parent, title, ModalityType.MODELESS);

        // Override the icon image to prevent it from being displayed
        setIconImage(null);

        // Create the main panel with a border layout and spacing
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Add a label with the provided message at the top
        panel.add(new JLabel("   " + message), BorderLayout.NORTH);

        // Initialize the progress bar in indeterminate (moving) mode
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        // Add the progress bar to the center of the panel
        panel.add(progressBar, BorderLayout.CENTER);

        // Add the panel to the dialog's content pane
        getContentPane().add(panel);

        // Prevent the dialog from being closed by the user
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Set the dialog size
        setSize(300, 100);

        // Center the dialog relative to the parent window
        setLocationRelativeTo(parent);
    }
}