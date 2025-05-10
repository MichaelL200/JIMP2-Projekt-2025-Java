package graphdivider.gui.navigation;

//import graphdivider.gui.navigation.AppNavigator;

import javax.swing.*;
import java.awt.*;

// Navigator with CardLayout
public class AppNavigator implements Navigator
{
    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel container;

    public AppNavigator()
    {
        frame = new JFrame("GraphDivider");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        frame.setContentPane(container);
    }

    // Register the screen using screen id
    public void register(String screenId, JPanel screen)
    {
        container.add(screen, screenId);
    }

    @Override
    public void show(String screenId)
    {
        cardLayout.show(container, screenId);
        if (!frame.isVisible())
        {
            SwingUtilities.invokeLater(() -> frame.setVisible(true));
        }
    }

    // Return the main frame
    public JFrame getFrame()
    {
        return frame;
    }
}
