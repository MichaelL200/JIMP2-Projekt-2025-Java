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

// Main application window
public final class Frame extends JFrame implements PropertyChangeListener
{
    // The main panel responsible for graph visualization.
    private final Graph graphPanel;
    // Controller for handling graph-related logic.
    private final GraphController controller;
    // Tracks the last known dark mode state to detect theme changes.
    private boolean lastDarkMode;

    // Enum to represent different types of graph loading
    private enum GraphLoadType
    {
        TEXT, PARTITIONED_TEXT, PARTITIONED_BINARY
    }

    //Constructs the main application window and initializes all UI components and listeners
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

    // Creates a JScrollPane to wrap the graph panel
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

    // Switches the application theme
    private void switchTheme(Theme.ThemeMode mode)
    {
        Theme.applyTheme(mode);
        updateWindowIcon(Theme.isDarkPreferred());
        SwingUtilities.updateComponentTreeUI(graphPanel);
    }

    // Sets the initial window size based on the screen dimensions
    private void setInitialWindowSize()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int halfWidth  = screenSize.width / 2;
        int twoThirdsHeight = (int) (screenSize.height / 1.5);
        setMinimumSize(new Dimension(halfWidth, twoThirdsHeight));
        setSize(halfWidth, twoThirdsHeight);
    }

    // Handles theme changes
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

    // Updates the window icon based on the current theme
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

    // Asynchronously loads a graph file and displays it in the graph panel
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

            // Executes the graph loading in a background thread
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

            // Updates the progress dialog when the loading is complete
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
}
