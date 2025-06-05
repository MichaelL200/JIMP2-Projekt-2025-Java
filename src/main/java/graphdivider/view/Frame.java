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
    // Panels and UI components
    private final Graph graphPanel;
    private final ToolPanel toolPanel;
    private final PartitionPanel partitionPanel;
    private final MenuBar menuBar;

    // Theme and controller state
    private boolean lastDarkMode;
    private Theme.ThemeMode lastThemeMode = null;
    private GraphController controller;

    public Frame()
    {
        setTitle("Graph Divider");
        lastDarkMode = Theme.isDarkPreferred();
        lastThemeMode = getCurrentThemeMode();
        updateWindowIcon(lastDarkMode);

        // Listen for Windows dark mode changes
        Toolkit.getDefaultToolkit().addPropertyChangeListener("win.menu.dark", this);

        boolean isWSL = isRunningUnderWSL();
        if (isWSL)
        {
            System.out.println("[Frame] Detected WSL environment. Starting theme polling.");
            startWSLThemePolling();
        }

        setInitialWindowSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        maximizeWindow(isWSL);

        // Fix window bounds on maximize
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

        // Setup menu bar
        menuBar = new MenuBar();
        setJMenuBar(menuBar);

        // Setup tool and partition panels (left side)
        toolPanel = new ToolPanel();
        partitionPanel = new PartitionPanel();
        Box leftBox = Box.createVerticalBox();
        leftBox.add(toolPanel);
        leftBox.add(Box.createVerticalStrut(10));
        leftBox.add(partitionPanel);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(leftBox, BorderLayout.WEST);

        // Setup graph panel with scrollbars (center)
        graphPanel = new Graph(toolPanel);
        JScrollPane scrollPane = new JScrollPane(graphPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBar(new graphdivider.view.ui.ScrollBar(JScrollBar.VERTICAL));
        scrollPane.setHorizontalScrollBar(new graphdivider.view.ui.ScrollBar(JScrollBar.HORIZONTAL));
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(1800, 900));
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    // Set the controller (must be called before using controller)
    public void setController(GraphController controller)
    {
        this.controller = controller;
    }

    // Set window title based on file
    public void setWindowTitleForFile(java.io.File file)
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

    // Handle loading a text graph file
    public void handleLoadTextGraph()
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

            // Disable controls while loading
            toolPanel.setDivideButtonEnabled(false);
            toolPanel.getPartitionsSpinner().setEnabled(false);
            toolPanel.getMarginSpinner().setEnabled(false);
            menuBar.setSaveButtons(false);

            // Check controller
            if (controller == null)
            {
                JOptionPane.showMessageDialog(this, "Controller is not initialized. Please restart the application.", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("[Frame] Controller is null. Aborting load.");
                return;
            }

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
                            partitionPanel.clear();
                            System.out.println("[Frame] Graph loaded successfully. Displaying...");
                            ProgressDialog progressDialog = new ProgressDialog(Frame.this, "Displaying Graph", "Displaying graph, please wait...");
                            // Display graph in background
                            SwingWorker<Void, Void> displayer = new SwingWorker<>()
                            {
                                @Override
                                protected Void doInBackground()
                                {
                                    System.out.println("[Frame] Displaying graph on panel...");
                                    controller.displayGraph(loaded.model);
                                    return null;
                                }

                                @Override
                                protected void done()
                                {
                                    System.out.println("[Frame] Graph display complete.");
                                    progressDialog.dispose();
                                    setWindowTitleForFile(selectedFile);

                                    // Enable controls after loading
                                    toolPanel.getPartitionsSpinner().setEnabled(true);
                                    toolPanel.getMarginSpinner().setEnabled(true);
                                    toolPanel.setDivideButtonEnabled(true);
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

    // Handle loading a partitioned text graph
    public void handleLoadPartitionedTextGraph()
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

    // Handle loading a partitioned binary graph
    public void handleLoadPartitionedBinaryGraph()
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

    // Switch between dark and light theme
    public void switchTheme(boolean dark)
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

    // Get current theme mode using reflection
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

    // Detect if running under WSL
    private boolean isRunningUnderWSL()
    {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("linux") && System.getenv("WSL_DISTRO_NAME") != null;
    }

    // Set initial window size
    private void setInitialWindowSize()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int halfWidth  = screenSize.width / 2;
        int halfHeight = (int) (screenSize.height / 1.5);
        setMinimumSize(new Dimension(halfWidth, halfHeight));
        setSize(halfWidth, halfHeight);
    }

    // Maximize window (special handling for WSL)
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

    // Start polling for theme changes in WSL
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

    // Handle Windows dark mode property change
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

    // Update window icon for current theme
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

    // Getters for panels and menu bar
    public ToolPanel getToolPanel() { return toolPanel; }
    public PartitionPanel getPartitionPanel() { return partitionPanel; }
    public MenuBar getAppMenuBar() { return menuBar; }
    public Graph getGraphPanel() { return graphPanel; }

    // Update tool panel's max partitions and spinner value
    public void updateToolPanelPartitions(int maxPartitions, int spinnerValue)
    {
        getToolPanel().setMaxPartitions(maxPartitions);
        getToolPanel().setPartitionsSpinnerValue(spinnerValue);
    }

    // Enable/disable tool panel controls
    public void setToolPanelEnabled(boolean enabled)
    {
        getToolPanel().setDivideButtonEnabled(enabled);
        getToolPanel().getPartitionsSpinner().setEnabled(enabled);
        getToolPanel().getMarginSpinner().setEnabled(enabled);
    }

    // Update partition panel with new values
    public void updatePartitionPanel(int edgesCut, double marginKept)
    {
        getPartitionPanel().setEdgesCut(edgesCut);
        getPartitionPanel().setMarginKept(marginKept);
    }
}
