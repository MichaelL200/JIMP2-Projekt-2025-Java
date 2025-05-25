package graphdivider.view.ui;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JDialog
{
    private final JProgressBar progressBar;

    /**
     * Creates a modal progress dialog with a message and indeterminate progress bar.
     *
     * @param parent  The parent window (can be JFrame or JDialog)
     * @param title   The dialog window title
     * @param message The message to display above the progress bar
     */
    public ProgressDialog(Window parent, String title, String message)
    {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel("   " + message), BorderLayout.NORTH);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.CENTER);
        getContentPane().add(panel);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setSize(300, 100);
        setLocationRelativeTo(parent);
        setAlwaysOnTop(true); // Ensure the dialog stays above the main window
    }
}
