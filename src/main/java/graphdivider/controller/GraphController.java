package graphdivider.controller;

import graphdivider.model.CSRmatrix;
import graphdivider.model.GraphLoader;
import graphdivider.model.GraphModel;
import graphdivider.view.ui.Theme;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

// Controller for handling graph-related actions and business logic.
public final class GraphController
{
    private graphdivider.view.ui.Graph graphView;

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
            // Print Graph Model data with color
            final String ANSI_CYAN = "\u001B[36m";
            final String ANSI_MAGENTA = "\u001B[35m";
            final String ANSI_RESET = "\u001B[0m";
            System.out.println(ANSI_CYAN + "\tGRAPH MODEL DATA:" + ANSI_RESET);
            model.printGraphData();

            CSRmatrix matrix = GraphLoader.toCSRmatrix(model);

            // Print Laplacian data with color
            System.out.println(ANSI_MAGENTA + "\tGRAPH LAPLACIAN DATA:" + ANSI_RESET);
            CSRmatrix laplacian = GraphLoader.toLaplacianCSRmatrix(model);

            return new LoadedGraph(model, matrix, laplacian);
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

        toolPanel.addDivideButtonListener(e ->
        {
            
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
