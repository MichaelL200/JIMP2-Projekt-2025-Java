package graphdivider;

import graphdivider.gui.navigation.AppNavigator;
import graphdivider.gui.navigation.Navigator;
import graphdivider.gui.screen.StartScreen;
import graphdivider.gui.widget.Screen;

import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
        Navigator nav = new AppNavigator();

        // Zarejestruj ekrany
        ((AppNavigator) nav).register("START", new StartScreen(nav));

        // Pokaż ekran startowy
        nav.show("START");
    }
}
