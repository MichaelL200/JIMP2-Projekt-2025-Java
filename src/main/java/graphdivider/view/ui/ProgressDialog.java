package graphdivider.view.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Simple progress dialog with an indeterminate progress bar.
 * Used to indicate ongoing operations where completion time is unknown.
 */
public final class ProgressDialog extends JDialog
{
    // Progress bar shown in dialog
    private final JProgressBar progressBar;

    /**
     * Constructs a non-modal progress dialog with a title and message.
     * The dialog is centered on the parent window and cannot be closed by the user.
     *
     * @param parent  The parent window to center the dialog on.
     * @param title   The title of the dialog window.
     * @param message The message to display above the progress bar.
     */
    public ProgressDialog(Window parent, String title, String message)
    {
        // Non-modal dialog with title
        super(parent, title, ModalityType.MODELESS);

        // Hide window icon
        setIconImage(null);

        // Main panel with border layout and spacing
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Message label at top
        panel.add(new JLabel("   " + message), BorderLayout.NORTH);

        // Indeterminate progress bar (shows ongoing activity)
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

        // Center dialog on parent window
        setLocationRelativeTo(parent);
    }
}