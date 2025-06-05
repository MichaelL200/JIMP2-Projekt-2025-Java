package graphdivider.controller;

import graphdivider.model.*;
import graphdivider.view.ui.ProgressDialog;
import graphdivider.view.ui.Theme;
import graphdivider.view.ui.graph.GraphColoring;
import graphdivider.view.ui.graph.Vertex;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

// Controller for graph actions and logic
public final class GraphController
{
    // Reference to graph view
    private graphdivider.view.ui.Graph graphView;

    // Loaded graph data
    private LoadedGraph loadedGraph;

    // Set the graph view
    public void setGraphView(graphdivider.view.ui.Graph view)
    {
        this.graphView = view;
    }

    // Show graph model in the view
    public void displayGraph(graphdivider.model.GraphModel model)
    {
        if (graphView != null)
        {
            graphView.displayGraph(model);
        }
    }

    // Load graph from file and return model, matrix, laplacian
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

            // Update tool panel state
            int vertexCount = model.getRowPositions().length;
            int maxPartitions = Math.min(100, vertexCount < 8 ? 2 : vertexCount / 4);
            if (parent instanceof graphdivider.view.Frame)
            {
                graphdivider.view.Frame frame = (graphdivider.view.Frame) parent;
                frame.updateToolPanelPartitions(maxPartitions, 2);
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

    // Register listeners on view components
    public void registerViewListeners(graphdivider.view.Frame frame)
    {
        graphdivider.view.ui.MenuBar menuBar = frame.getAppMenuBar();
        graphdivider.view.ui.ToolPanel toolPanel = frame.getToolPanel();
        graphdivider.view.ui.Graph graphPanel = this.graphView;

        menuBar.addLightThemeListener(e -> {
            System.out.println("[MenuBar] Light theme selected.");
            frame.switchTheme(false);
        });

        menuBar.addDarkThemeListener(e -> {
            System.out.println("[MenuBar] Dark theme selected.");
            frame.switchTheme(true);
        });

        menuBar.addAutoThemeListener(e -> {
            System.out.println("[MenuBar] Auto theme selected.");
            Theme.applyAutoTheme(() -> {
                frame.switchTheme(Theme.isDarkPreferred());
            });
        });

        menuBar.addLoadTextGraphListener(e -> {
            System.out.println("[MenuBar] Load text graph selected.");
            frame.handleLoadTextGraph();
        });
        menuBar.addLoadPartitionedTextListener(e -> {
            System.out.println("[MenuBar] Load partitioned text graph selected.");
            frame.handleLoadPartitionedTextGraph();
        });
        menuBar.addLoadPartitionedBinaryListener(e -> {
            System.out.println("[MenuBar] Load partitioned binary graph selected.");
            frame.handleLoadPartitionedBinaryGraph();
        });

        toolPanel.addDivideButtonActionListener(e -> {
            try
            {
                if (this.loadedGraph == null)
                {
                    throw new IllegalStateException("No graph loaded.");
                }

                // Disable tool panel controls
                frame.setToolPanelEnabled(false);

                int numParts = toolPanel.getPartitions();

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

                            int numParts = toolPanel.getPartitions();
                            int[] clusters = GraphClusterization.clusterizeGraph(eigenresult, numParts);

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

                            graphView.updateClusters(clusters);

                            frame.updatePartitionPanel(edgesCut, marginKept);

                            // Tool panel stays disabled after dividing
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

    // Holds loaded graph model, matrix, and Laplacian
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