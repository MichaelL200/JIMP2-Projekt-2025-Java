package graphdivider.gui.widget;

import graphdivider.gui.navigation.Navigator;

import javax.swing.*;
import java.awt.*;

public abstract class Screen extends JPanel
{
    protected final Navigator navigator;

    public Screen(Navigator navigator)
    {
        this.navigator = navigator;
        initScreen();
    }

    private void initScreen()
    {
        // Default center layout
        setLayout(new GridBagLayout());
        // shared background
        setBackground(Color.DARK_GRAY);
        // full-screen wrapper if embedding in JFrame
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    }

    public JFrame createFrame()
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setContentPane(this);
        return frame;
    }
}
