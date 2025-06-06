package graphdivider.view;

import graphdivider.controller.GraphController;
import graphdivider.view.ui.*;
import graphdivider.view.ui.MenuBar;

import javax.swing.*;
import java.awt.*;
import java.io.File;

// Main window for the Graph Divider tool.
public final class Frame extends JFrame
{
    // Panels and UI components
    private final Graph graphPanel;
    private final ToolPanel toolPanel;
    private final PartitionPanel partitionPanel;
    private final MenuBar menuBar;

    // Theme and controller state
    private GraphController controller;

    public Frame()
    {
        setTitle("Graph Divider");

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
            File selectedPartitionedFile = fileChooser.getSelectedFile();
            File baseFile;
            try
            {
                baseFile = graphdivider.io.Input.getBaseGraphFile(selectedPartitionedFile);
            } catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "Could not determine base graph: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println("[Frame] Loading base graph: " + baseFile.getAbsolutePath());
            setWindowTitleForFile(selectedPartitionedFile);

            if (controller == null)
            {
                JOptionPane.showMessageDialog(this, "Controller is not initialized. Please restart the application.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SwingWorker<GraphController.LoadedGraph, Void> loader = new SwingWorker<>()
            {
                @Override
                protected GraphController.LoadedGraph doInBackground()
                {
                    return controller.loadGraphFromFile(Frame.this, baseFile);
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
                            partitionPanel.setUnknown();
                            ProgressDialog progressDialog = new ProgressDialog(Frame.this, "Displaying Graph", "Displaying graph, please wait...");
                            SwingWorker<Void, Void> displayer = new SwingWorker<>()
                            {
                                @Override
                                protected Void doInBackground()
                                {
                                    controller.displayGraph(loaded.model);
                                    try
                                    {
                                        int[] clusters = graphdivider.io.Input.loadClustersForGraph(selectedPartitionedFile);
                                        graphPanel.updateClusters(clusters);
                                    } catch (Exception ex)
                                    {
                                        System.out.println("[Frame] Failed to load clusters: " + ex.getMessage());
                                    }
                                    return null;
                                }

                                @Override
                                protected void done()
                                {
                                    progressDialog.dispose();
                                    toolPanel.getPartitionsSpinner().setEnabled(false);
                                    toolPanel.getMarginSpinner().setEnabled(false);
                                    toolPanel.setDivideButtonEnabled(false);
                                    menuBar.setSaveButtons(false);
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
            File selectedPartitionedFile = fileChooser.getSelectedFile();
            File baseFile;
            try
            {
                baseFile = graphdivider.io.Input.getBaseGraphFileForBin(selectedPartitionedFile);
            } catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "Could not determine base graph: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println("[Frame] Loading base graph: " + baseFile.getAbsolutePath());
            setWindowTitleForFile(selectedPartitionedFile);

            if (controller == null)
            {
                JOptionPane.showMessageDialog(this, "Controller is not initialized. Please restart the application.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SwingWorker<GraphController.LoadedGraph, Void> loader = new SwingWorker<>()
            {
                @Override
                protected GraphController.LoadedGraph doInBackground()
                {
                    return controller.loadGraphFromFile(Frame.this, baseFile);
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
                            partitionPanel.setUnknown();
                            ProgressDialog progressDialog = new ProgressDialog(Frame.this, "Displaying Graph", "Displaying graph, please wait...");
                            SwingWorker<Void, Void> displayer = new SwingWorker<>()
                            {
                                @Override
                                protected Void doInBackground()
                                {
                                    controller.displayGraph(loaded.model);
                                    try
                                    {
                                        int[] clusters = graphdivider.io.Input.loadClustersForBin(selectedPartitionedFile);
                                        graphPanel.updateClusters(clusters);
                                    } catch (Exception ex)
                                    {
                                        System.out.println("[Frame] Failed to load clusters: " + ex.getMessage());
                                    }
                                    return null;
                                }

                                @Override
                                protected void done()
                                {
                                    progressDialog.dispose();
                                    toolPanel.getPartitionsSpinner().setEnabled(false);
                                    toolPanel.getMarginSpinner().setEnabled(false);
                                    toolPanel.setDivideButtonEnabled(false);
                                    menuBar.setSaveButtons(false);
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
        else
        {
            System.out.println("[Frame] File chooser cancelled or closed.");
        }
    }

    // Update window icon for current theme
    public void updateWindowIcon()
    {
        setIconImage(Theme.loadSystemAwareWindowIcon());
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