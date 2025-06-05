package graphdivider.view.ui;

import javax.swing.*;
import java.awt.*;

// Simple progress dialog with indeterminate bar
public final class ProgressDialog extends JDialog
{
    // Progress bar shown in dialog
    private final JProgressBar progressBar;

    public ProgressDialog(Window parent, String title, String message)
    {
        // Non-modal dialog with title
        super(parent, title, ModalityType.MODELESS);

        // Hide window icon
        setIconImage(null);

        // Main panel with border layout
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Message label at top
        panel.add(new JLabel("   " + message), BorderLayout.NORTH);

        // Indeterminate progress bar
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        // Add progress bar to panel
        panel.add(progressBar, BorderLayout.CENTER);

        // Add panel to dialog
        getContentPane().add(panel);

        // Prevent user from closing dialog
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Set dialog size
        setSize(300, 100);

        // Center dialog on parent
        setLocationRelativeTo(parent);
    }
}