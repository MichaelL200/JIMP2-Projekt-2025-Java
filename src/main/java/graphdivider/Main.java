package graphdivider;

import graphdivider.gui.navigation.AppNavigator;
import graphdivider.gui.navigation.Navigator;
import graphdivider.gui.screen.StartScreen;

/**
 * Entry point for the GraphDivider application.
 * <p>
 * Sets up navigation and displays the initial screen.
 */
public class Main
{
    /**
     * The main method initializes navigation, registers screens and shows the starting view.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args)
    {
        // Create the Navigator that will manage screen transitions
        Navigator nav = new AppNavigator();

        // Register the StartScreen under the key "START", so the Navigator knows which panel to display
        ((AppNavigator) nav).register("START", new StartScreen(nav));

        // Show the initial screen; this will also make the window visible
        nav.show("START");
    }
}
