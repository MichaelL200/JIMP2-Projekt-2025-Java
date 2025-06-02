package graphdivider.controller;

import graphdivider.model.CSRmatrix;
import graphdivider.model.GraphLoader;
import graphdivider.model.GraphModel;
import graphdivider.model.GraphPartitioner;
import graphdivider.view.ui.Theme;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
            CSRmatrix matrix = GraphLoader.toCSRmatrix(model);
            CSRmatrix laplacian = GraphLoader.toLaplacianCSRmatrix(model);

            this.loadedGraph = new LoadedGraph(model, matrix, laplacian);

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

                // Compute 2 smallest eigenpairs
                CSRmatrix laplacian = this.loadedGraph.laplacian;
                GraphPartitioner.EigenResult eigenresult = GraphPartitioner.computeSmallestEigenpairs(laplacian, 2);
                GraphPartitioner.printEigenData(eigenresult);
            } catch (Exception ex)
            {
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