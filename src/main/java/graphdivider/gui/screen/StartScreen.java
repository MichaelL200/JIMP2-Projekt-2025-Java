package graphdivider.gui.screen;

import graphdivider.gui.navigation.Navigator;
import graphdivider.gui.widget.Button;
import graphdivider.gui.widget.Screen;

import javax.swing.*;
import java.awt.*;

public class StartScreen extends Screen
{
    private final Button button1, button2;

    public StartScreen(Navigator nav)
    {
        super(nav);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 50, 20, 50);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;

        button1 = new Button("Wczytaj graf");
        c.gridx = 0; add(button1, c);

        button2 = new Button("Wczytaj wynik podziału");
        c.gridx = 1; add(button2, c);

        button1.addActionListener(e -> navigator.show("GRAPH"));
        button2.addActionListener(e -> navigator.show("RESULT"));
    }
}
