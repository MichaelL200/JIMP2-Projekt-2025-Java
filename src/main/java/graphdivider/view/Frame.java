package graphdivider.view;

import graphdivider.model.CSRmatrix;
import graphdivider.view.ui.MenuBar;
import graphdivider.view.ui.Theme;
import graphdivider.view.ui.ToolPanel;
import graphdivider.view.ui.Graph;
import graphdivider.model.GraphLoader;
import graphdivider.model.GraphModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * Main application window for the Graph Divider tool.
 * Applies the Observer and Command patterns for theme and UI logic.
 */
public class Frame extends JFrame implements PropertyChangeListener
{
    private boolean lastDarkMode;
    private final Graph graphPanel;

    public Frame()
    {
        setTitle("Graph Divider");
        lastDarkMode = Theme.isDarkPreferred();
        updateWindowIcon(lastDarkMode);

        // Register as observer for theme changes (Windows 11+)
        Toolkit.getDefaultToolkit().addPropertyChangeListener("win.menu.dark", this);

        // Detect if running under WSL and start theme polling if so
        boolean isWSL = isRunningUnderWSL();
        if (isWSL)
        {
            startWSLThemePolling();
        }

        setInitialWindowSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        maximizeWindow(isWSL);

        MenuBar menuBar = new MenuBar();
        setJMenuBar(menuBar);

        ToolPanel toolPanel = new ToolPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolPanel, BorderLayout.WEST);

        graphPanel = new Graph();
        getContentPane().add(graphPanel, BorderLayout.CENTER);

        // Theme switching via menu bar listeners
        menuBar.addLightThemeListener(e -> switchTheme(false));
        menuBar.addDarkThemeListener(e -> switchTheme(true));
        menuBar.addAutoThemeListener(e -> Theme.applyAutoTheme(() ->
        {
            updateWindowIcon(Theme.isDarkPreferred());
            SwingUtilities.updateComponentTreeUI(graphPanel);
            graphPanel.repaint();
        }));

        // Add file chooser for loading text graphs
        menuBar.addLoadTextGraphListener(e ->
        {
            JFileChooser fileChooser = new JFileChooser("src/main/resources/graphs/");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR Graph files (*.csrrg)", "csrrg"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                try
                {
                    GraphModel model = GraphLoader.loadFromFile(selectedFile);
                    CSRmatrix adjacencyMatrix = GraphLoader.toCSRmatrix(model);
                    // TODO: Pass model to graphPanel for visualization
                }
                catch (IOException ex)
                {
                    JOptionPane.showMessageDialog(this, "Failed to load graph: " + ex.getMessage(),
                            "Load Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add file chooser for loading text divided graphs
        menuBar.addLoadPartitionedTextListener(e ->
        {
            JFileChooser fileChooser = new JFileChooser("src/main/resources/divided_graphs/");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR (Partitioned) Graph files (*.csrrg)", "csrrg"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                // TODO: Load and process the selected .csrrg file
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            }
        });

        // Add file chooser for loading binary divided graphs
        menuBar.addLoadPartitionedBinaryListener(e ->
        {
            JFileChooser fileChooser = new JFileChooser("src/main/resources/divided_graphs/");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR (Partitioned) Graph Binary files (*.bin)", "bin"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                // TODO: Load and process the selected .csrrg file
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            }
        });

        // Listen for changes in tool panel spinners
        toolPanel.addChangeListener(e ->
        {
            int parts = toolPanel.getPartitions();
            int margin = toolPanel.getMargin();
            // TODO: Pass these values to the controller for further processing
            System.out.println("Partitions: " + parts + ", Margin: " + margin + "%");
        });

        // Listen for Divide Graph button press
        toolPanel.addDivideButtonListener(e -> 
        {
            // TODO: Implement graph division logic here
            System.out.println("Divide Graph button pressed.");
        });
    }

    /**
     * Switches the application theme and updates the icon.
     * @param dark true for dark theme, false for light theme
     */
    private void switchTheme(boolean dark)
    {
        if (dark)
        {
            Theme.applyDarkTheme();
        }
        else
        {
            Theme.applyLightTheme();
        }
        updateWindowIcon(Theme.isDarkPreferred());
        SwingUtilities.updateComponentTreeUI(graphPanel);
        graphPanel.repaint(); // Ensures edge color updates
    }

    /**
     * Checks if the application is running under Windows Subsystem for Linux.
     * @return true if running under WSL, false otherwise
     */
    private boolean isRunningUnderWSL()
    {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("linux") && System.getenv("WSL_DISTRO_NAME") != null;
    }

    /**
     * Sets the initial window size to half the screen width and 2/3 height.
     */
    private void setInitialWindowSize()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int halfWidth  = screenSize.width / 2;
        int halfHeight = (int) (screenSize.height / 1.5);
        setMinimumSize(new Dimension(halfWidth, halfHeight));
        setSize(halfWidth, halfHeight);
    }

    /**
     * Maximizes the window, with a workaround for WSL.
     * @param isWSL true if running under WSL
     */
    private void maximizeWindow(boolean isWSL)
    {
        if (isWSL)
        {
            SwingUtilities.invokeLater(() ->
            {
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0)
                {
                    new javax.swing.Timer(100, e ->
                    {
                        setExtendedState(JFrame.MAXIMIZED_BOTH);
                        revalidate();
                        repaint();
                        ((javax.swing.Timer) e.getSource()).stop();
                    }).start();
                }
            });
        }
        else
        {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    /**
     * Starts polling for theme changes under WSL.
     */
    private void startWSLThemePolling()
    {
        Timer wslThemeTimer = new Timer(2000, null);
        wslThemeTimer.addActionListener(e ->
        {
            boolean dark = Theme.isDarkPreferred();
            if (dark != lastDarkMode)
            {
                updateWindowIcon(dark);
                lastDarkMode = dark;
            }
        });
        wslThemeTimer.setRepeats(true);
        wslThemeTimer.start();
    }

    /**
     * Observer update for theme changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        boolean dark = Theme.isDarkPreferred();
        if (dark != lastDarkMode)
        {
            updateWindowIcon(dark);
            lastDarkMode = dark;
        }
    }

    /**
     * Updates the window icon depending on the theme.
     *
     * @param darkMode true to use icon_dark.png, false to use icon_light.png
     */
    public void updateWindowIcon(boolean darkMode)
    {
        String resource = darkMode ? "/icon_dark.png" : "/icon_light.png";
        try
        {
            java.io.InputStream iconStream = getClass().getResourceAsStream(resource);
            if (iconStream == null)
            {
                throw new IllegalArgumentException("Resource not found: " + resource);
            }
            Image icon = ImageIO.read(iconStream);
            setIconImage(icon);
        }
        catch (IOException | IllegalArgumentException e)
        {
            System.err.println("Warning: Unable to load window icon '" + resource + "': " + e.getMessage());
        }
    }
}
