package graphdivider.controller;

import graphdivider.model.*;
import graphdivider.view.Theme;
import graphdivider.view.Language;
import graphdivider.view.ui.ProgressDialog;
import graphdivider.view.ui.graph.GraphColoring;
import graphdivider.view.ui.graph.Vertex;
import graphdivider.io.Output;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Controller for graph actions and logic.
 * Handles loading, displaying, partitioning, and saving graphs.
 */
public final class GraphController
{
    // Reference to graph view panel
    private graphdivider.view.ui.Graph graphView;

    // Loaded graph data (model, matrix, laplacian)
    private LoadedGraph loadedGraph;

    // Store last partitioning info for default filename
    private String lastInputFilename = null;
    private int lastNumParts = 2;
    private int lastEdgesCut = 0;

    // Adjacency matrix after partitioning
    private CSRmatrix adjacencyDivided = null;

    /**
     * Sets the graph view panel for this controller.
     * 
     * @param view The Graph panel to control.
     */
    public void setGraphView(graphdivider.view.ui.Graph view)
    {
        this.graphView = view;
    }

    /**
     * Displays the given graph model in the view.
     * 
     * @param model The GraphModel to display.
     */
    public void displayGraph(graphdivider.model.GraphModel model)
    {
        if (graphView != null)
        {
            graphView.displayGraph(model);
        }
    }

    /**
     * Loads a graph from file and returns its model, matrix, and laplacian.
     * Updates tool panel state and stores loaded graph.
     * 
     * @param parent Parent JFrame for dialogs.
     * @param file File to load.
     * @return LoadedGraph object with model, matrix, and laplacian, or null on error.
     */
    public LoadedGraph loadGraphFromFile(JFrame parent, File file)
    {
        System.out.println("[GraphController] Loading graph from file: " + file.getAbsolutePath());
        try
        {
            GraphModel model = GraphLoader.loadFromFile(file);
            model.printGraphData();

            CSRmatrix matrix = GraphLoader.toCSRmatrix(model);
            matrix.printAdjacency();

            CSRmatrix laplacian = GraphLoader.toLaplacianCSRmatrix(model);
            laplacian.printLaplacian();

            this.loadedGraph = new LoadedGraph(model, matrix, laplacian);

            // Store input filename for default save name
            this.lastInputFilename = file.getName();

            // Update tool panel state
            int vertexCount = model.getRowPositions().length;
            int maxPartitions = Math.min(100, vertexCount < 8 ? 2 : vertexCount / 4);
            if (parent instanceof graphdivider.view.Frame)
            {
                graphdivider.view.Frame frame = (graphdivider.view.Frame) parent;
                frame.updateToolPanelPartitions(maxPartitions, 2);
                // Set partition panel to unknown after loading
                frame.getPartitionPanel().setUnknown();
            } 
            else
            {
                throw new IllegalArgumentException("Parent is not an instance of Frame.");
            }

            return this.loadedGraph;
        } 
        catch (IOException ex)
        {
            System.out.println("[GraphController] Failed to load graph: " + ex.getMessage());
            JOptionPane.showMessageDialog(parent, "Failed to load graph: " + ex.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Registers listeners on view components for menu and tool panel actions.
     * Handles theme, language, file loading, saving, and partitioning actions.
     * 
     * @param frame The main application frame.
     */
    public void registerViewListeners(graphdivider.view.Frame frame)
    {
        graphdivider.view.ui.MenuBar menuBar = frame.getAppMenuBar();
        graphdivider.view.ui.ToolPanel toolPanel = frame.getToolPanel();
        graphdivider.view.ui.Graph graphPanel = this.graphView;

        menuBar.addLightThemeMenuItemListener(e ->
        {
            System.out.println("[MenuBar] Light theme selected.");
            Theme.setLightTheme();
            frame.updateWindowIcon();
        });

        menuBar.addDarkThemeMenuItemListener(e ->
        {
            System.out.println("[MenuBar] Dark theme selected.");
            Theme.setDarkTheme();
            frame.updateWindowIcon();
        });

        menuBar.addSystemThemeMenuItemListener(e ->
        {
            System.out.println("[MenuBar] Auto theme selected.");
            Theme.setAutoTheme(() -> {
                frame.updateWindowIcon();
            });
        });

        menuBar.addEnglishLanguageMenuItemListener(e ->
        {
            System.out.println("[MenuBar] English language selected.");
            Language.applyEnglishLanguage();
        });

        menuBar.addPolishLanguageMenuItemListener(e ->
        {
            System.out.println("[MenuBar] Polish language selected.");
            Language.applyPolishLanguage();
        });

        menuBar.addLoadGraphTextMenuItemListener(e ->
        {
            System.out.println("[MenuBar] Load text graph selected.");
            frame.handleLoadTextGraph();
        });
        menuBar.addLoadPartitionedGraphTextMenuItemListener(e ->
        {
            System.out.println("[MenuBar] Load partitioned text graph selected.");
            frame.handleLoadPartitionedTextGraph();
        });
        menuBar.addLoadPartitionedGraphBinaryMenuItemListener(e ->
        {
            System.out.println("[MenuBar] Load partitioned binary graph selected.");
            frame.handleLoadPartitionedBinaryGraph();
        });

        // Register save listeners
        menuBar.addSavePartitionedGraphTextMenuItemListener(e ->
        {
            // Suggest default filename
            String baseName = (lastInputFilename != null ? lastInputFilename.replaceAll("\\.[^.]*$", "") : "graph");
            String defaultName = String.format("%s_parts%d_cut%d.csrrg2", baseName, lastNumParts, lastEdgesCut);

            // Show save dialog for text file
            JFileChooser fileChooser = new JFileChooser("src/main/resources/output");
            fileChooser.setDialogTitle("Save Partitioned Graph (Text)");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR Partitioned Graph files (*.csrrg2)", "csrrg2"));
            fileChooser.setSelectedFile(new java.io.File(defaultName));
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                java.io.File file = fileChooser.getSelectedFile();
                try {
                    // Pass the loaded graph model to Output.writeText
                    // In savePartitionedTextListener
                    Output.writeText(file, lastNumParts, lastEdgesCut, frame.getPartitionPanel().getMarginKept(),
                            this.loadedGraph != null ? this.loadedGraph.model : null, adjacencyDivided);
                    System.out.println("[MenuBar] Saving partitioned graph as text to: " + file.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Failed to save file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        menuBar.addSavePartitionedGraphBinaryMenuItemListener(e ->
        {
            // Suggest default filename
            String baseName = (lastInputFilename != null ? lastInputFilename.replaceAll("\\.[^.]*$", "") : "graph");
            String defaultName = String.format("%s_parts%d_cut%d.bin", baseName, lastNumParts, lastEdgesCut);

            // Show save dialog for binary file
            JFileChooser fileChooser = new JFileChooser("src/main/resources/output");
            fileChooser.setDialogTitle("Save Partitioned Graph (Binary)");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR Partitioned Graph Binary files (*.bin)", "bin"));
            fileChooser.setSelectedFile(new java.io.File(defaultName));
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                java.io.File file = fileChooser.getSelectedFile();
                try
                {
                    Output.writeBinary(file, lastNumParts, lastEdgesCut, frame.getPartitionPanel().getMarginKept(),
                            this.loadedGraph != null ? this.loadedGraph.model : null, adjacencyDivided);
                    System.out.println("[MenuBar] Saving partitioned graph as binary to: " + file.getAbsolutePath());
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Failed to save file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        toolPanel.addPartitionButtonActionListener(e ->
        {
            try
            {
                if (this.loadedGraph == null)
                {
                    throw new IllegalStateException("No graph loaded.");
                }

                // Disable tool panel controls
                frame.setToolPanelEnabled(false);

                int numParts = toolPanel.getPartitionCount();

                ProgressDialog progressDialog = new ProgressDialog(frame, "Partitioning Graph", "Calculating eigenvalues and eigenvectors...");
                progressDialog.setVisible(true);

                SwingWorker<GraphEigenvalues.EigenResult, Void> worker = new SwingWorker<>()
                {
                    @Override
                    protected GraphEigenvalues.EigenResult doInBackground() throws Exception
                    {
                        CSRmatrix laplacian = loadedGraph.laplacian;
                        return GraphEigenvalues.computeSmallestEigenpairs(laplacian, numParts);
                    }

                    @Override
                    protected void done()
                    {
                        try
                        {
                            GraphEigenvalues.EigenResult eigenresult = get();
                            GraphEigenvalues.printEigenData(eigenresult);

                            int numParts = toolPanel.getPartitionCount();
                            int[] clusters = GraphClusterization.clusterizeGraph(eigenresult, numParts);

                            adjacencyDivided = CSRmatrix.maskCutEdges(loadedGraph.matrix, clusters);

                            Vertex[] vertices = graphView.getVertices();
                            if (vertices == null || clusters == null)
                            {
                                throw new IllegalArgumentException("Vertices or clusters are null.");
                            }
                            if (vertices.length != clusters.length)
                            {
                                System.err.println("Vertices length: " + vertices.length);
                                System.err.println("Clusters length: " + clusters.length);
                                throw new IllegalArgumentException("Vertices and clusters must have the same length.");
                            }

                            GraphClusterization.printClusters(clusters);

                            // Calculate edges cut BEFORE updating clusters
                            int edgesCut = GraphColoring.calculateEdgesCut(vertices, clusters, graphView.getEdges());
                            double marginKept = GraphClusterization.calculateMargin(clusters, numParts);

                            // Store for default filename
                            lastNumParts = numParts;
                            lastEdgesCut = edgesCut;

                            graphView.updateClusters(clusters);

                            frame.updatePartitionPanel(edgesCut, marginKept);

                            // Enable save buttons after partitioning
                            frame.getAppMenuBar().setSaveMenuItemsEnabled(true);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "Error computing eigenpairs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            // Re-enable controls on error
                            frame.setToolPanelEnabled(true);
                        } finally
                        {
                            progressDialog.dispose();
                        }
                    }
                };

                worker.execute();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error computing eigenpairs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                frame.setToolPanelEnabled(true);
            }
        });
    }

    /**
     * Handles loading a text graph file (called from Frame).
     * Opens file chooser, loads graph, and displays it.
     * 
     * @param frame The main application frame.
     */
    public void loadTextGraph(graphdivider.view.Frame frame)
    {
        System.out.println("[Controller] Opening file chooser for text graph...");
        JFileChooser fileChooser = new JFileChooser("src/main/resources/graphs/");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR Graph files (*.csrrg)", "csrrg"));
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            System.out.println("[Controller] Selected file: " + selectedFile.getAbsolutePath());

            // Disable controls while loading
            frame.getToolPanel().setPartitionButtonEnabled(false);
            frame.getToolPanel().getPartitionCountSpinner().setEnabled(false);
            frame.getToolPanel().getPartitionMarginSpinner().setEnabled(false);
            frame.getAppMenuBar().setSaveMenuItemsEnabled(false);

            // Set partition panel to unknown at the beginning
            frame.getPartitionPanel().setUnknown();

            // Load graph in background
            SwingWorker<LoadedGraph, Void> loader = new SwingWorker<>()
            {
                @Override
                protected LoadedGraph doInBackground()
                {
                    System.out.println("[Controller] Loading graph from file (background): " + selectedFile.getAbsolutePath());
                    return loadGraphFromFile(frame, selectedFile);
                }

                @Override
                protected void done()
                {
                    try
                    {
                        LoadedGraph loaded = get();
                        if (loaded != null)
                        {
                            System.out.println("[Controller] Graph loaded successfully. Displaying...");
                            ProgressDialog progressDialog = new ProgressDialog(frame, "Displaying Graph", "Displaying graph, please wait...");
                            // Display graph in background
                            SwingWorker<Void, Void> displayer = new SwingWorker<>()
                            {
                                @Override
                                protected Void doInBackground()
                                {
                                    System.out.println("[Controller] Displaying graph on panel...");
                                    displayGraph(loaded.model);
                                    return null;
                                }

                                @Override
                                protected void done()
                                {
                                    System.out.println("[Controller] Graph display complete.");
                                    progressDialog.dispose();
                                    frame.setWindowTitleForFile(selectedFile);

                                    // Enable controls after loading
                                    frame.getToolPanel().getPartitionCountSpinner().setEnabled(true);
                                    frame.getToolPanel().getPartitionMarginSpinner().setEnabled(true);
                                    frame.getToolPanel().setPartitionButtonEnabled(true);
                                }
                            };

                            displayer.execute();
                            progressDialog.setVisible(true);
                        } else
                        {
                            System.out.println("[Controller] Failed to load graph.");
                            JOptionPane.showMessageDialog(frame, "Failed to load graph.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex)
                    {
                        System.out.println("[Controller] Exception while loading graph: " + ex.getMessage());
                        JOptionPane.showMessageDialog(frame, "Failed to load graph: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            loader.execute();
        }
        else
        {
            System.out.println("[Controller] File chooser cancelled or closed.");
        }
    }

    /**
     * Handles loading a partitioned text graph (called from Frame).
     * Loads the base graph and applies cluster assignments.
     * 
     * @param frame The main application frame.
     */
    public void loadPartitionedTextGraph(graphdivider.view.Frame frame)
    {
        System.out.println("[Controller] Opening file chooser for partitioned text graph...");
        JFileChooser fileChooser = new JFileChooser("src/main/resources/divided_graphs/");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR (Partitioned) Graph files (*.csrrg)", "csrrg"));
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File selectedPartitionedFile = fileChooser.getSelectedFile();
            File baseFile;
            try
            {
                baseFile = graphdivider.io.Input.getBaseGraphFile(selectedPartitionedFile);
            } catch (Exception ex)
            {
                JOptionPane.showMessageDialog(frame, "Could not determine base graph: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println("[Controller] Loading base graph: " + baseFile.getAbsolutePath());
            frame.setWindowTitleForFile(selectedPartitionedFile);

            SwingWorker<LoadedGraph, Void> loader = new SwingWorker<>()
            {
                @Override
                protected LoadedGraph doInBackground()
                {
                    return loadGraphFromFile(frame, baseFile);
                }

                @Override
                protected void done()
                {
                    try
                    {
                        LoadedGraph loaded = get();
                        if (loaded != null)
                        {
                            frame.getPartitionPanel().clear();
                            frame.getPartitionPanel().setUnknown();
                            ProgressDialog progressDialog = new ProgressDialog(frame, "Displaying Graph", "Displaying graph, please wait...");
                            SwingWorker<Void, Void> displayer = new SwingWorker<>()
                            {
                                @Override
                                protected Void doInBackground()
                                {
                                    displayGraph(loaded.model);
                                    try
                                    {
                                        int[] clusters = graphdivider.io.Input.loadClustersForGraph(selectedPartitionedFile);
                                        graphView.updateClusters(clusters);
                                    } catch (Exception ex)
                                    {
                                        System.out.println("[Controller] Failed to load clusters: " + ex.getMessage());
                                    }
                                    return null;
                                }

                                @Override
                                protected void done()
                                {
                                    progressDialog.dispose();
                                    frame.getToolPanel().getPartitionCountSpinner().setEnabled(false);
                                    frame.getToolPanel().getPartitionMarginSpinner().setEnabled(false);
                                    frame.getToolPanel().setPartitionButtonEnabled(false);
                                    frame.getAppMenuBar().setSaveMenuItemsEnabled(false);
                                }
                            };
                            displayer.execute();
                            progressDialog.setVisible(true);
                        } else
                        {
                            JOptionPane.showMessageDialog(frame, "Failed to load graph.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex)
                    {
                        JOptionPane.showMessageDialog(frame, "Failed to load graph: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            loader.execute();
        }
        else
        {
            System.out.println("[Controller] File chooser cancelled or closed.");
        }
    }

    /**
     * Handles loading a partitioned binary graph (called from Frame).
     * Loads the base graph and applies cluster assignments.
     * 
     * @param frame The main application frame.
     */
    public void loadPartitionedBinaryGraph(graphdivider.view.Frame frame)
    {
        System.out.println("[Controller] Opening file chooser for partitioned binary graph...");
        JFileChooser fileChooser = new JFileChooser("src/main/resources/divided_graphs/");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSR (Partitioned) Graph Binary files (*.bin)", "bin"));
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File selectedPartitionedFile = fileChooser.getSelectedFile();
            File baseFile;
            try
            {
                baseFile = graphdivider.io.Input.getBaseGraphFileForBin(selectedPartitionedFile);
            } catch (Exception ex)
            {
                JOptionPane.showMessageDialog(frame, "Could not determine base graph: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println("[Controller] Loading base graph: " + baseFile.getAbsolutePath());
            frame.setWindowTitleForFile(selectedPartitionedFile);

            SwingWorker<LoadedGraph, Void> loader = new SwingWorker<>()
            {
                @Override
                protected LoadedGraph doInBackground()
                {
                    return loadGraphFromFile(frame, baseFile);
                }

                @Override
                protected void done()
                {
                    try
                    {
                        LoadedGraph loaded = get();
                        if (loaded != null)
                        {
                            frame.getPartitionPanel().clear();
                            frame.getPartitionPanel().setUnknown();
                            ProgressDialog progressDialog = new ProgressDialog(frame, "Displaying Graph", "Displaying graph, please wait...");
                            SwingWorker<Void, Void> displayer = new SwingWorker<>()
                            {
                                @Override
                                protected Void doInBackground()
                                {
                                    displayGraph(loaded.model);
                                    try
                                    {
                                        int[] clusters = graphdivider.io.Input.loadClustersForBin(selectedPartitionedFile);
                                        graphView.updateClusters(clusters);
                                    } catch (Exception ex)
                                    {
                                        System.out.println("[Controller] Failed to load clusters: " + ex.getMessage());
                                    }
                                    return null;
                                }

                                @Override
                                protected void done()
                                {
                                    progressDialog.dispose();
                                    frame.getToolPanel().getPartitionCountSpinner().setEnabled(false);
                                    frame.getToolPanel().getPartitionMarginSpinner().setEnabled(false);
                                    frame.getToolPanel().setPartitionButtonEnabled(false);
                                    frame.getAppMenuBar().setSaveMenuItemsEnabled(false);
                                }
                            };
                            displayer.execute();
                            progressDialog.setVisible(true);
                        } else
                        {
                            JOptionPane.showMessageDialog(frame, "Failed to load graph.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex)
                    {
                        JOptionPane.showMessageDialog(frame, "Failed to load graph: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            loader.execute();
        }
        else
        {
            System.out.println("[Controller] File chooser cancelled or closed.");
        }
    }

    /**
     * Holds loaded graph model, matrix, and Laplacian.
     */
    public static class LoadedGraph
    {
        // Graph model (structure)
        public final GraphModel model;
        // Adjacency matrix (CSR)
        public final CSRmatrix matrix;
        // Laplacian matrix (CSR)
        public final CSRmatrix laplacian;

        /**
         * Constructs a LoadedGraph with model, matrix, and laplacian.
         * 
         * @param model GraphModel object.
         * @param matrix CSRmatrix adjacency matrix.
         * @param laplacian CSRmatrix Laplacian matrix.
         */
        public LoadedGraph(GraphModel model, CSRmatrix matrix, CSRmatrix laplacian)
        {
            this.model = model;
            this.matrix = matrix;
            this.laplacian = laplacian;
        }
    }
}
