package graphdivider.view;

import graphdivider.controller.GraphController;
import graphdivider.view.ui.*;
import graphdivider.view.ui.MenuBar;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * Main application window for the Graph Divider tool.
 *
 * Responsibilities:
 * - Hosts the main UI components (menu, tool panel, graph visualization).
 * - Handles theme switching and adapts to OS theme changes (Observer pattern).
 * - Delegates business logic (file loading, partitioning) to the controller.
 */
public final class Frame extends JFrame implements PropertyChangeListener
{
    // The main panel responsible for graph visualization.
    private final Graph graphPanel;
    // Controller for handling graph-related logic.
    private final GraphController controller = new GraphController();
    // Tracks the last known dark mode state to detect theme changes.
    private boolean lastDarkMode;

    /**
     * Constructs the main application window and initializes all UI components and listeners.
     */
    public Frame()
    {
        setTitle("Graph Divider");
        lastDarkMode = Theme.isDarkPreferred();
        updateWindowIcon(lastDarkMode);

        // Register as observer for Windows theme changes (Windows 11+).
        Toolkit.getDefaultToolkit().addPropertyChangeListener("win.menu.dark", this);

        // Detect if running under WSL (Windows Subsystem for Linux) and start polling for theme changes if so.
        boolean isWSL = isRunningUnderWSL();
        if (isWSL)
        {
            startWSLThemePolling();
        }

        setInitialWindowSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        maximizeWindow(isWSL);

        // Workaround: Force size to usable screen area when maximized (for laptops with maximization issues)
        this.addWindowStateListener(e -> {
            if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                GraphicsConfiguration gc = getGraphicsConfiguration();
                Rectangle bounds = gc.getBounds();
                Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
                int x = bounds.x + insets.left;
                int y = bounds.y + insets.top;
                int width = bounds.width - insets.left - insets.right;
                int height = bounds.height - insets.top - insets.bottom;
                setBounds(x, y, width, height); // Only fill usable area, not covering taskbar
                revalidate();
                repaint();
            }
        });

        // Set up the menu bar with theme and file loading actions.
        MenuBar menuBar = new MenuBar();
        setJMenuBar(menuBar);

        // Set up the tool panel for partition settings and graph division controls.
        ToolPanel toolPanel = new ToolPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolPanel, BorderLayout.WEST);

        // Set up the main graph visualization panel.
        graphPanel = new Graph(toolPanel);
        getContentPane().add(graphPanel, BorderLayout.CENTER);

        // Register listeners for theme switching via the menu bar.
        menuBar.addLightThemeListener(e -> switchTheme(false));
        menuBar.addDarkThemeListener(e -> switchTheme(true));
        menuBar.addAutoThemeListener(e -> Theme.applyAutoTheme(() ->
        {
            // Callback to update icon and UI when auto theme changes.
            updateWindowIcon(Theme.isDarkPreferred());
            SwingUtilities.updateComponentTreeUI(graphPanel);
            graphPanel.repaint();
        }));

        // Register listener for loading a text-based graph file.
        menuBar.addLoadTextGraphListener(e -> handleLoadTextGraph());

        // Register listener for loading a partitioned text graph file.
        menuBar.addLoadPartitionedTextListener(e -> handleLoadPartitionedTextGraph());

        // Register listener for loading a partitioned binary graph file.
        menuBar.addLoadPartitionedBinaryListener(e -> handleLoadPartitionedBinaryGraph());

        // Listen for changes in the tool panel's spinners (number of partitions and margin).
        toolPanel.addChangeListener(e ->
        {
            int parts = toolPanel.getPartitions();
            int margin = toolPanel.getMargin();
            // Delegate to controller if business logic is needed
            // controller.handlePartitionSettings(parts, margin);
            System.out.println("Partitions: " + parts + ", Margin: " + margin + "%");
        });

        // Listen for Divide Graph button press to trigger graph partitioning.
        toolPanel.addDivideButtonListener(e ->
        {
            // Delegate to controller for partitioning logic
            // controller.divideGraph(...);
            System.out.println("Divide Graph button pressed.");
        });
    }

    /**
     * Handles loading a text-based graph file using the controller.
     * Shows a progress bar while displaying the graph.
     */
    private void handleLoadTextGraph()
    {
        JFileChooser fileChooser = new JFileChooser("src/main/resources/graphs/");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR Graph files (*.csrrg)", "csrrg"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            // Load the graph model in background
            SwingWorker<GraphController.LoadedGraph, Void> loader = new SwingWorker<>()
            {
                @Override
                protected GraphController.LoadedGraph doInBackground()
                {
                    return controller.loadGraphFromFile(Frame.this, selectedFile);
                }

                @Override
                protected void done()
                {
                    try
                    {
                        GraphController.LoadedGraph loaded = get();
                        if (loaded != null)
                        {
                            // Use ProgressDialog for displaying phase
                            ProgressDialog progressDialog = new ProgressDialog(Frame.this, "Displaying Graph...", "Displaying graph, please wait...");
                            SwingWorker<Void, Void> displayer = new SwingWorker<>()
                            {
                                @Override
                                protected Void doInBackground()
                                {
                                    graphPanel.displayGraph(loaded.model);
                                    return null;
                                }

                                @Override
                                protected void done()
                                {
                                    progressDialog.dispose();
                                }
                            };

                            displayer.execute();
                            progressDialog.setVisible(true);
                        } else
                        {
                            JOptionPane.showMessageDialog(Frame.this, "Failed to load graph.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex)
                    {
                        JOptionPane.showMessageDialog(Frame.this, "Failed to load graph: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            loader.execute();
        }
    }

    /**
     * Handles loading a partitioned text graph file.
     * Extend the controller to support this if needed.
     */
    private void handleLoadPartitionedTextGraph()
    {
        JFileChooser fileChooser = new JFileChooser("src/main/resources/divided_graphs/");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR (Partitioned) Graph files (*.csrrg)", "csrrg"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            // Example: controller.loadPartitionedGraphFromFile(this, selectedFile);
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    /**
     * Handles loading a partitioned binary graph file.
     * Extend the controller to support this if needed.
     */
    private void handleLoadPartitionedBinaryGraph()
    {
        JFileChooser fileChooser = new JFileChooser("src/main/resources/divided_graphs/");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR (Partitioned) Graph Binary files (*.bin)", "bin"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            // Example: controller.loadPartitionedBinaryGraphFromFile(this, selectedFile);
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    /**
     * Switches the application theme between light and dark modes.
     * Updates the window icon and repaints the graph panel to reflect the new theme.
     *
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
     * Checks if the application is running under Windows Subsystem for Linux (WSL).
     * Used to determine if theme polling is necessary.
     *
     * @return true if running under WSL, false otherwise
     */
    private boolean isRunningUnderWSL()
    {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("linux") && System.getenv("WSL_DISTRO_NAME") != null;
    }

    /**
     * Sets the initial window size to half the screen width and two-thirds of the screen height.
     * Ensures the window is large enough for comfortable use.
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
     * Maximizes the window, with a workaround for WSL where maximizing must be delayed.
     *
     * @param isWSL true if running under WSL, false otherwise
     */
    private void maximizeWindow(boolean isWSL)
    {
        if (isWSL)
        {
            // In WSL, maximize after a short delay to ensure proper sizing.
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
     * Starts a timer to poll for theme changes under WSL, since native theme events are not available.
     * Updates the window icon if the theme changes.
     */
    private void startWSLThemePolling()
    {
        // Timer checks every 2 seconds for theme changes.
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
     * Called when the OS theme changes (Windows 11+).
     * Updates the window icon if the theme has changed.
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
     * Updates the window icon depending on the current theme.
     * Loads either the dark or light icon resource.
     *
     * @param darkMode true to use icon_dark.png, false to use icon_light.png
     */
    public void updateWindowIcon(boolean darkMode)
    {
        String resource = darkMode ? "/icon/icon_dark.png" : "/icon/icon_light.png";
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
