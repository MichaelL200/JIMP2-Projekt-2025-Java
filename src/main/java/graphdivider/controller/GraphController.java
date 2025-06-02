package graphdivider.controller;

import graphdivider.model.*;
import graphdivider.view.ui.Theme;
import graphdivider.view.ui.ProgressDialog;
import graphdivider.view.ui.graph.GraphColoring;
import graphdivider.view.ui.graph.Vertex;

import javax.swing.*;
import javax.swing.SwingWorker;
import java.io.File;
import java.io.IOException;

// Controller for handling graph-related actions and business logic.
public final class GraphController
{
    private graphdivider.view.ui.Graph graphView;

    // Field to store the loaded graph
    private LoadedGraph loadedGraph;

    // Set the Graph view for this controller
    public void setGraphView(graphdivider.view.ui.Graph view)
    {
        this.graphView = view;
    }

    // Display the graph model on the view
    public void displayGraph(graphdivider.model.GraphModel model)
    {
        if (graphView != null)
        {
            graphView.displayGraph(model);
        }
    }

    // Loads a graph from the given file and returns the model, matrix, and Laplacian matrix.
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

            // Update the maximum number of partitions in the tool panel
            int vertexCount = model.getRowPositions().length;
            int maxPartitions = Math.min(100, vertexCount < 8 ? 2 : vertexCount / 4);
            if (parent instanceof graphdivider.view.Frame)
            {
                graphdivider.view.Frame frame = (graphdivider.view.Frame) parent;
                frame.getToolPanel().setMaxPartitions(maxPartitions);
                frame.getToolPanel().setPartitionsSpinnerValue(2);
            } else
            {
                throw new IllegalArgumentException("Parent is not an instance of Frame.");
            }

            return this.loadedGraph;
        } catch (IOException ex)
        {
            System.out.println("[GraphController] Failed to load graph: " + ex.getMessage());
            JOptionPane.showMessageDialog(parent, "Failed to load graph: " + ex.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // Register all listeners on the view components.
    public void registerViewListeners(graphdivider.view.Frame frame)
    {
        graphdivider.view.ui.MenuBar menuBar = frame.getAppMenuBar();
        graphdivider.view.ui.ToolPanel toolPanel = frame.getToolPanel();
        graphdivider.view.ui.Graph graphPanel = this.graphView;

        menuBar.addLightThemeListener(e ->
        {
            System.out.println("[MenuBar] Light theme selected.");
            frame.switchTheme(false); // Use the Frame's switchTheme method
        });

        menuBar.addDarkThemeListener(e ->
        {
            System.out.println("[MenuBar] Dark theme selected.");
            frame.switchTheme(true); // Use the Frame's switchTheme method
        });

        menuBar.addAutoThemeListener(e ->
        {
            System.out.println("[MenuBar] Auto theme selected.");
            Theme.applyAutoTheme(() -> {
                frame.switchTheme(Theme.isDarkPreferred());
            });
        });
        menuBar.addLoadTextGraphListener(e ->
        {
            System.out.println("[MenuBar] Load text graph selected.");
            frame.handleLoadTextGraph();
        });
        menuBar.addLoadPartitionedTextListener(e ->
        {
            System.out.println("[MenuBar] Load partitioned text graph selected.");
            frame.handleLoadPartitionedTextGraph();
        });
        menuBar.addLoadPartitionedBinaryListener(e ->
        {
            System.out.println("[MenuBar] Load partitioned binary graph selected.");
            frame.handleLoadPartitionedBinaryGraph();
        });
        menuBar.addLightThemeListener(e ->
        {
            Theme.applyLightTheme();
            frame.updateWindowIcon(false);
            SwingUtilities.updateComponentTreeUI(graphPanel);
            graphPanel.repaint();
        });
        menuBar.addDarkThemeListener(e ->
        {
            Theme.applyDarkTheme();
            frame.updateWindowIcon(true);
            SwingUtilities.updateComponentTreeUI(graphPanel);
            graphPanel.repaint();
        });
        menuBar.addAutoThemeListener(e ->
        {
            Theme.applyAutoTheme(() ->
            {
                frame.updateWindowIcon(Theme.isDarkPreferred());
                SwingUtilities.updateComponentTreeUI(graphPanel);
                graphPanel.repaint();
            });
        });
        menuBar.addLoadTextGraphListener(e ->
        {

        });

        toolPanel.addDivideButtonActionListener(e ->
        {
            try
            {
                if (this.loadedGraph == null)
                {
                    throw new IllegalStateException("No graph loaded.");
                }

                // Retrieve the number of parts from the tool panel
                int numParts = toolPanel.getPartitions();

                // Show progress dialog
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

                            // Perform graph clusterization
                            int numParts = toolPanel.getPartitions();
                            int[] clusters = GraphClusterization.clusterizeGraph(eigenresult, numParts);

                            // Validate clusters and vertices
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

                            // Print the clusters
                            GraphClusterization.printClusters(clusters);

                            // Divide the graph into clusters in the view
                            GraphColoring.colorVertices(graphView.getVertices(), clusters, graphView.getEdges());
                            graphView.repaint();
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "Error computing eigenpairs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            }
        });

        graphPanel.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e)
            {
                frame.clearGraphPanel();
            }
        });
    }

    // Data holder for loaded graph model, matrix, and Laplacian matrix.
    public static class LoadedGraph
    {
        public final GraphModel model;
        public final CSRmatrix matrix;
        public final CSRmatrix laplacian;

        public LoadedGraph(GraphModel model, CSRmatrix matrix, CSRmatrix laplacian)
        {
            this.model = model;
            this.matrix = matrix;
            this.laplacian = laplacian;
        }
    }
}