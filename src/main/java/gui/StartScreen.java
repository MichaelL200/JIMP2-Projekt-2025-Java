package gui;

import javax.swing.*;
import java.awt.*;

public class StartScreen extends JFrame
{
    private final JButton button1;
    private final JButton button2;

    public StartScreen()
    {
        super("Start Screen");

        // Full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Ensure exit on close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set the title
        setTitle("Options - GraphDivider");

        // Create buttons
        button1 = new JButton("Wczytaj graf");
        button2 = new JButton("Wczytaj wynik podziału");

        // Center the buttons
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 20));
        panel.add(button1);
        panel.add(button2);

        // Add padding around buttons
        panel.setBorder(BorderFactory.createEmptyBorder(200, 0, 200, 0));

        setContentPane(panel);
        pack();
        // In case pack() makes it smaller, force full size again
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // Expose the buttons so callers can add ActionListeners
    public JButton getButton1()
    {
        return button1;
    }
    public JButton getButton2()
    {
        return button2;
    }
}
