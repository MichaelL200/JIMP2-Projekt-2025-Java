package graphdivider.gui.screen;

import graphdivider.gui.navigation.Navigator;
import graphdivider.gui.widget.Button;
import graphdivider.gui.widget.Screen;

import javax.swing.*;
import java.awt.*;

/**
 * The initial screen shown when the application starts.
 * <p>
 * Uses GridBagLayout to place two buttons that expand
 * proportionally when the window is resized.
 */
public class StartScreen extends Screen
{
    /** Button to load a graph from an input file. */
    private final Button button1;
    /** Button to load the result file produced by the C program. */
    private final Button button2;

    /**
     * Constructs the StartScreen and lays out its components.
     *
     * @param nav the Navigator used to switch between screens
     */
    public StartScreen(Navigator nav)
    {
        // Pass the navigator reference up to the Screen base
        super(nav);

        // Prepare layout constraints for both buttons:
        // - Insets: 20px top/bottom, 50px left/right
        // - Fill: both horizontally and vertically
        // - weightx: share extra horizontal space equally
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 50, 20, 50);
        c.fill   = GridBagConstraints.BOTH;
        c.weightx = 1.0;

        // Create and add the "Wczytaj graf" button in column 0
        button1 = new Button("Wczytaj graf");
        c.gridx = 0;
        add(button1, c);

        // Create and add the "Wczytaj wynik podziału" button in column 1
        button2 = new Button("Wczytaj wynik podziału");
        c.gridx = 1;
        add(button2, c);

        // Register click handlers:
        // - When button1 is clicked, navigate to the GRAPH screen
        button1.addActionListener(e -> navigator.show("GRAPH"));
        // - When button2 is clicked, navigate to the RESULT screen
        button2.addActionListener(e -> navigator.show("RESULT"));
    }
}
