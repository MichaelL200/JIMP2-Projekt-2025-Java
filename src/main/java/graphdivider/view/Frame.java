package graphdivider.view;

import graphdivider.controller.GraphController;
import graphdivider.view.ui.*;
import graphdivider.view.ui.MenuBar;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
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
    private final GraphController controller;
    // Tracks the last known dark mode state to detect theme changes.
    private boolean lastDarkMode;

    /**
     * Constructs the main application window and initializes all UI components and listeners.
     * @param controller the GraphController to use for business logic
     */
    public Frame(GraphController controller)
    {
        this.controller = controller;
        setTitle("Graph Divider");
        lastDarkMode = Theme.isDarkPreferred();
        updateWindowIcon(lastDarkMode);

        // Register as observer for Windows theme changes (Windows 11+).
        Toolkit.getDefaultToolkit().addPropertyChangeListener("win.menu.dark", this);

        // Delegate WSL theme polling to Theme
        Theme.startWSLThemePolling(this::updateWindowIcon);

        // Set the initial window size and maximize it.
        setInitialWindowSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Force size to usable screen area when maximized (for laptops with maximization issues)
        this.addWindowStateListener(e ->
        {
            if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH)
            {
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

        // Wrap the graphPanel in a JScrollPane to enable scrollbars for large graphs
        JScrollPane scrollPane = getScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Register listeners for theme switching via the menu bar.
        menuBar.addLightThemeListener(e -> switchTheme(Theme.ThemeMode.LIGHT));
        menuBar.addDarkThemeListener(e -> switchTheme(Theme.ThemeMode.DARK));
        menuBar.addAutoThemeListener(e -> Theme.applyAutoTheme(() ->
        {
            // Callback to update icon and UI when auto theme changes.
            updateWindowIcon(Theme.isDarkPreferred());
            SwingUtilities.updateComponentTreeUI(graphPanel);
            graphPanel.repaint();
        }));

        // Register listener for loading a text-based graph file.
        menuBar.addLoadTextGraphListener(e ->
        {
            JFileChooser fileChooser = new JFileChooser("src/main/resources/graphs/");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR Graph files (*.csrrg)", "csrrg"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = fileChooser.getSelectedFile();
                loadGraphAsync(selectedFile, GraphLoadType.TEXT);
            }
        });

        // Register listener for loading a partitioned text graph file.
        menuBar.addLoadPartitionedTextListener(e ->
        {
            JFileChooser fileChooser = new JFileChooser("src/main/resources/divided_graphs/");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR (Partitioned) Graph files (*.csrrg)", "csrrg"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = fileChooser.getSelectedFile();
                loadGraphAsync(selectedFile, GraphLoadType.PARTITIONED_TEXT);
            }
        });

        // Register listener for loading a partitioned binary graph file.
        menuBar.addLoadPartitionedBinaryListener(e ->
        {
            JFileChooser fileChooser = new JFileChooser("src/main/resources/divided_graphs/");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR (Partitioned) Graph Binary files (*.bin)", "bin"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = fileChooser.getSelectedFile();
                loadGraphAsync(selectedFile, GraphLoadType.PARTITIONED_BINARY);
            }
        });

        // Listen for changes in the tool panel's spinners (number of partitions and margin).
        toolPanel.addChangeListener(e ->
        {
            int parts = toolPanel.getPartitions();
            int margin = toolPanel.getMargin();
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
     * Creates a JScrollPane to wrap the graph panel.
     * This allows for scrollbars when the graph exceeds the viewport size.
     * Uses custom scrollbars for a modern look and feel.
     *
     * @return a JScrollPane containing the graph panel
     */
    private JScrollPane getScrollPane()
    {
        JScrollPane scrollPane = new JScrollPane(graphPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // Use custom wide scrollbars
        scrollPane.setVerticalScrollBar(new ScrollBar(JScrollBar.VERTICAL));
        scrollPane.setHorizontalScrollBar(new ScrollBar(JScrollBar.HORIZONTAL));
        // Remove border for a cleaner look
        scrollPane.setBorder(null);
        // Set a wider preferred viewport size for the scroll pane
        scrollPane.setPreferredSize(new Dimension(1800, 900));
        return scrollPane;
    }

    /**
     * Switches the application theme.
     * Updates the window icon and repaints the graph panel to reflect the new theme.
     *
     * @param mode ThemeMode to apply
     */
    private void switchTheme(Theme.ThemeMode mode)
    {
        Theme.applyTheme(mode);
        updateWindowIcon(Theme.isDarkPreferred());
        SwingUtilities.updateComponentTreeUI(graphPanel);
    }

    /**
     * Sets the initial window size to half the screen width and two-thirds of the screen height.
     * Ensures the window is large enough for comfortable use.
     */
    private void setInitialWindowSize()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int halfWidth  = screenSize.width / 2;
        int twoThirdsHeight = (int) (screenSize.height / 1.5);
        setMinimumSize(new Dimension(halfWidth, twoThirdsHeight));
        setSize(halfWidth, twoThirdsHeight);
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
        try (java.io.InputStream iconStream = getClass().getResourceAsStream(resource))
        {
            if (iconStream == null)
            {
                throw new IllegalArgumentException("Resource not found: " + resource);
            }
            Image icon = ImageIO.read(iconStream);
            setIconImage(icon);
        } catch (IOException | IllegalArgumentException e)
        {
            System.err.println("Warning: Unable to load window icon '" + resource + "': " + e.getMessage());
        }
    }

    /**
     * Loads a graph file asynchronously, showing a progress dialog.
     * Delegates file loading and graph display to the controller.
     */
    private void loadGraphAsync(File selectedFile, GraphLoadType type)
    {
        String title, message;
        switch (type)
        {
            case TEXT ->
            {
                title = "Loading Graph...";
                message = "Loading graph, please wait...";
            }
            case PARTITIONED_TEXT ->
            {
                title = "Loading Partitioned Graph...";
                message = "Loading partitioned graph, please wait...";
            }
            case PARTITIONED_BINARY ->
            {
                title = "Loading Partitioned Binary Graph...";
                message = "Loading partitioned binary graph, please wait...";
            }
            default ->
            {
                title = "Loading...";
                message = "Loading, please wait...";
            }
        }
        ProgressDialog progressDialog = new ProgressDialog(this, title, message);
        progressDialog.setModalityType(Dialog.ModalityType.MODELESS);

        SwingWorker<Void, Void> loader = new SwingWorker<>()
        {
            private Exception error = null;

            /**
             * Background task to load the graph file.
             * Runs in a separate thread to avoid blocking the Event Dispatch Thread.
             * Displays a progress dialog while loading.
             *
             * @return null when done
             */
            @Override
            protected Void doInBackground()
            {
                try
                {
                    controller.loadAndDisplayGraph(selectedFile, type, graphPanel);
                } catch (Exception ex)
                {
                    error = ex;
                    cancel(true);
                }
                return null;
            }

            /**
             * Called on the Event Dispatch Thread after doInBackground completes.
             * Disposes the progress dialog and shows an error message if loading failed.
             */
            @Override
            protected void done()
            {
                progressDialog.dispose();
                if (error != null || isCancelled())
                {
                    JOptionPane.showMessageDialog(Frame.this, "Failed to load graph." +
                                    (error != null ? "\n" + error.getMessage() : ""),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        SwingUtilities.invokeLater(() ->
        {
            progressDialog.setVisible(true);
            loader.execute();
        });
    }

    /**
     * Enum for graph load types.
     */
    private enum GraphLoadType
    {
        TEXT, PARTITIONED_TEXT, PARTITIONED_BINARY
    }
}
