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

// Main window for the Graph Divider tool.
public final class Frame extends JFrame implements PropertyChangeListener
{
    // Graph panel
    private final Graph graphPanel;
    // Controller
    private final GraphController controller = new GraphController();
    // Last dark mode state
    private boolean lastDarkMode;

    // Constructs the main window and initializes UI.
    public Frame()
    {
        setTitle("Graph Divider");
        lastDarkMode = Theme.isDarkPreferred();
        updateWindowIcon(lastDarkMode);

        // Listen for Windows theme changes
        Toolkit.getDefaultToolkit().addPropertyChangeListener("win.menu.dark", this);

        // Detect WSL and start polling if needed
        boolean isWSL = isRunningUnderWSL();
        if (isWSL)
        {
            startWSLThemePolling();
        }

        setInitialWindowSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        maximizeWindow(isWSL);

        // Workaround: fill usable area when maximized
        this.addWindowStateListener(e -> {
            if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                GraphicsConfiguration gc = getGraphicsConfiguration();
                Rectangle bounds = gc.getBounds();
                Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
                int x = bounds.x + insets.left;
                int y = bounds.y + insets.top;
                int width = bounds.width - insets.left - insets.right;
                int height = bounds.height - insets.top - insets.bottom;
                setBounds(x, y, width, height);
                revalidate();
                repaint();
            }
        });

        // Menu bar
        MenuBar menuBar = new MenuBar();
        setJMenuBar(menuBar);

        // Tool panel
        ToolPanel toolPanel = new ToolPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolPanel, BorderLayout.WEST);

        // Graph visualization panel
        graphPanel = new Graph(toolPanel);

        // Scroll pane for graph panel
        JScrollPane scrollPane = new JScrollPane(graphPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // Custom scrollbars
        scrollPane.setVerticalScrollBar(new graphdivider.view.ui.ScrollBar(JScrollBar.VERTICAL));
        scrollPane.setHorizontalScrollBar(new graphdivider.view.ui.ScrollBar(JScrollBar.HORIZONTAL));
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(1800, 900));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Theme switching listeners
        menuBar.addLightThemeListener(e -> switchTheme(false));
        menuBar.addDarkThemeListener(e -> switchTheme(true));
        menuBar.addAutoThemeListener(e -> Theme.applyAutoTheme(() ->
        {
            updateWindowIcon(Theme.isDarkPreferred());
            SwingUtilities.updateComponentTreeUI(graphPanel);
            graphPanel.repaint();
        }));

        // Load text graph listener
        menuBar.addLoadTextGraphListener(e -> handleLoadTextGraph());

        // Load partitioned text graph listener
        menuBar.addLoadPartitionedTextListener(e -> handleLoadPartitionedTextGraph());

        // Load partitioned binary graph listener
        menuBar.addLoadPartitionedBinaryListener(e -> handleLoadPartitionedBinaryGraph());

        // Tool panel spinner listeners
        toolPanel.addChangeListener(e ->
        {
            int parts = toolPanel.getPartitions();
            int margin = toolPanel.getMargin();
            // controller.handlePartitionSettings(parts, margin);
            System.out.println("Partitions: " + parts + ", Margin: " + margin + "%");
        });

        // Divide Graph button listener
        toolPanel.addDivideButtonListener(e ->
        {
            // controller.divideGraph(...);
            System.out.println("Divide Graph button pressed.");
        });
    }

    // Sets the window title based on the loaded file
    private void setWindowTitleForFile(java.io.File file) {
        if (file != null) {
            setTitle("Graph Divider - " + file.getName());
        } else {
            setTitle("Graph Divider");
        }
    }

    // Loads a text-based graph file.
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

            // Load graph in background
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
                            // Show progress dialog while displaying
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
                                    setWindowTitleForFile(selectedFile); // <-- Universal title update
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

    // Loads a partitioned text graph file.
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
            setWindowTitleForFile(selectedFile); // <-- Universal title update
            // controller.loadPartitionedGraphFromFile(this, selectedFile);
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    // Loads a partitioned binary graph file.
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
            setWindowTitleForFile(selectedFile); // <-- Universal title update
            // controller.loadPartitionedBinaryGraphFromFile(this, selectedFile);
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    // Switches theme and updates icon/UI.
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
        graphPanel.repaint();
    }

    // Checks if running under WSL.
    private boolean isRunningUnderWSL()
    {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("linux") && System.getenv("WSL_DISTRO_NAME") != null;
    }

    // Sets initial window size.
    private void setInitialWindowSize()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int halfWidth  = screenSize.width / 2;
        int halfHeight = (int) (screenSize.height / 1.5);
        setMinimumSize(new Dimension(halfWidth, halfHeight));
        setSize(halfWidth, halfHeight);
    }

    // Maximizes window, workaround for WSL.
    private void maximizeWindow(boolean isWSL)
    {
        if (isWSL)
        {
            // In WSL, maximize after delay
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

    // Starts polling for theme changes under WSL.
    private void startWSLThemePolling()
    {
        // Timer checks every 2 seconds
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

    // Called on OS theme change.
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

    // Updates window icon for current theme.
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
