import gui.StartScreen;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("GraphDivider");

        // Start Screen
        StartScreen start = new StartScreen();
        start.getButton1().addActionListener(e -> { /* do A */ });
        start.getButton2().addActionListener(e -> { /* do B */ });
        start.setVisible(true);
    }
}
