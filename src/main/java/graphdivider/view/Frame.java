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
    // Track last applied theme mode to avoid redundant UI updates
    private Theme.ThemeMode lastThemeMode = null;

    // Constructs the main window and initializes UI.
    public Frame()
    {
        setTitle("Graph Divider");
        lastDarkMode = Theme.isDarkPreferred();
        lastThemeMode = getCurrentThemeMode();
        updateWindowIcon(lastDarkMode);

        // Listen for Windows theme changes
        Toolkit.getDefaultToolkit().addPropertyChangeListener("win.menu.dark", this);

        // Detect WSL and start polling if needed
        boolean isWSL = isRunningUnderWSL();
        if (isWSL)
        {
            System.out.println("[Frame] Detected WSL environment. Starting theme polling.");
            startWSLThemePolling();
        }

        setInitialWindowSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        maximizeWindow(isWSL);

        // Fill usable area when maximized
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

        // Add mouse listener to clear the graph when clicked
        graphPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                graphPanel.clearGraph();
                toolPanel.setDivideButtonEnabled(false);
                setTitle("Graph Divider");
            }
        });

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
        menuBar.addLightThemeListener(e ->
        {
            System.out.println("[MenuBar] Light theme selected.");
            switchTheme(false);
        });
        menuBar.addDarkThemeListener(e ->
        {
            System.out.println("[MenuBar] Dark theme selected.");
            switchTheme(true);
        });
        menuBar.addAutoThemeListener(e ->
        {
            System.out.println("[MenuBar] Auto theme selected.");
            Theme.applyAutoTheme(() ->
            {
                updateWindowIcon(Theme.isDarkPreferred());
                SwingUtilities.updateComponentTreeUI(graphPanel);
                graphPanel.repaint();
            });
        });
        menuBar.addLoadTextGraphListener(e ->
        {
            System.out.println("[MenuBar] Load text graph selected.");
            handleLoadTextGraph();
        });
        menuBar.addLoadPartitionedTextListener(e ->
        {
            System.out.println("[MenuBar] Load partitioned text graph selected.");
            handleLoadPartitionedTextGraph();
        });
        menuBar.addLoadPartitionedBinaryListener(e ->
        {
            System.out.println("[MenuBar] Load partitioned binary graph selected.");
            handleLoadPartitionedBinaryGraph();
        });
        toolPanel.addDivideButtonListener(e ->
        {
            System.out.println("[ToolPanel] Divide Graph button pressed.");
        });
    }

    // Sets the window title based on the loaded file
    private void setWindowTitleForFile(java.io.File file)
    {
        if (file != null)
        {
            System.out.println("[Frame] Setting window title for file: " + file.getName());
            setTitle("Graph Divider - " + file.getName());
        } else
        {
            setTitle("Graph Divider");
        }
    }

    // Loads a text-based graph file.
    private void handleLoadTextGraph()
    {
        System.out.println("[Frame] Opening file chooser for text graph...");
        JFileChooser fileChooser = new JFileChooser("src/main/resources/graphs/");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR Graph files (*.csrrg)", "csrrg"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            System.out.println("[Frame] Selected file: " + selectedFile.getAbsolutePath());

            // Load graph in background
            SwingWorker<GraphController.LoadedGraph, Void> loader = new SwingWorker<>()
            {
                @Override
                protected GraphController.LoadedGraph doInBackground()
                {
                    System.out.println("[Frame] Loading graph from file (background): " + selectedFile.getAbsolutePath());
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
                            System.out.println("[Frame] Graph loaded successfully. Displaying...");
                            // Show progress dialog while displaying
                            ProgressDialog progressDialog = new ProgressDialog(Frame.this, "Displaying Graph...", "Displaying graph, please wait...");
                            SwingWorker<Void, Void> displayer = new SwingWorker<>()
                            {
                                @Override
                                protected Void doInBackground()
                                {
                                    System.out.println("[Frame] Displaying graph on panel...");
                                    graphPanel.displayGraph(loaded.model);
                                    return null;
                                }

                                @Override
                                protected void done()
                                {
                                    System.out.println("[Frame] Graph display complete.");
                                    progressDialog.dispose();
                                    setWindowTitleForFile(selectedFile); // <-- Universal title update
                                }
                            };

                            displayer.execute();
                            progressDialog.setVisible(true);
                        } else
                        {
                            System.out.println("[Frame] Failed to load graph.");
                            JOptionPane.showMessageDialog(Frame.this, "Failed to load graph.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex)
                    {
                        System.out.println("[Frame] Exception while loading graph: " + ex.getMessage());
                        JOptionPane.showMessageDialog(Frame.this, "Failed to load graph: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            loader.execute();
        }
        else
        {
            System.out.println("[Frame] File chooser cancelled or closed.");
        }
    }

    // Loads a partitioned text graph file.
    private void handleLoadPartitionedTextGraph()
    {
        System.out.println("[Frame] Opening file chooser for partitioned text graph...");
        JFileChooser fileChooser = new JFileChooser("src/main/resources/divided_graphs/");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR (Partitioned) Graph files (*.csrrg)", "csrrg"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            System.out.println("[Frame] Selected partitioned text file: " + selectedFile.getAbsolutePath());
            setWindowTitleForFile(selectedFile);
            // controller.loadPartitionedGraphFromFile(this, selectedFile);
        }
        else
        {
            System.out.println("[Frame] File chooser cancelled or closed.");
        }
    }

    // Loads a partitioned binary graph file.
    private void handleLoadPartitionedBinaryGraph()
    {
        System.out.println("[Frame] Opening file chooser for partitioned binary graph...");
        JFileChooser fileChooser = new JFileChooser("src/main/resources/divided_graphs/");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR (Partitioned) Graph Binary files (*.bin)", "bin"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            System.out.println("[Frame] Selected partitioned binary file: " + selectedFile.getAbsolutePath());
            setWindowTitleForFile(selectedFile);
            // controller.loadPartitionedBinaryGraphFromFile(this, selectedFile);
        }
        else
        {
            System.out.println("[Frame] File chooser cancelled or closed.");
        }
    }

    // Switches theme and updates icon/UI only if theme actually changed.
    private void switchTheme(boolean dark)
    {
        Theme.ThemeMode desired = dark ? Theme.ThemeMode.DARK : Theme.ThemeMode.LIGHT;
        if (lastThemeMode == desired)
        {
            return;
        }
        System.out.println("[Frame] Switching theme to " + (dark ? "dark" : "light") + "...");
        if (dark)
        {
            Theme.applyDarkTheme();
        }
        else
        {
            Theme.applyLightTheme();
        }
        lastThemeMode = desired;
        updateWindowIcon(Theme.isDarkPreferred());
        SwingUtilities.updateComponentTreeUI(graphPanel);
        graphPanel.repaint();
    }

    // Helper to get the current theme mode
    private Theme.ThemeMode getCurrentThemeMode()
    {
        try
        {
            java.lang.reflect.Field f = Theme.class.getDeclaredField("currentTheme");
            f.setAccessible(true);
            return (Theme.ThemeMode) f.get(null);
        } catch (Exception e)
        {
            return null;
        }
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
            SwingUtilities.invokeLater(() ->
            {
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0)
                {
                    Timer timer = new javax.swing.Timer(100, e ->
                    {
                        setExtendedState(JFrame.MAXIMIZED_BOTH);
                        revalidate();
                        repaint();
                        ((Timer) e.getSource()).stop();
                    });
                    timer.setRepeats(false);
                    timer.start();
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
