package graphdivider.controller;

import graphdivider.model.CSRmatrix;
import graphdivider.model.GraphLoader;
import graphdivider.model.GraphModel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

// Controller for handling graph-related actions and business logic.
public final class GraphController
{
    // Loads a graph from the given file and returns the model, matrix, and Laplacian matrix.
    public LoadedGraph loadGraphFromFile(JFrame parent, File file)
    {
        System.out.println("[GraphController] Loading graph from file: " + file.getAbsolutePath());
        try
        {
            GraphModel model = GraphLoader.loadFromFile(file);
            model.printGraphData();
            CSRmatrix matrix = GraphLoader.toCSRmatrix(model);
            CSRmatrix laplacian = GraphLoader.toLaplacianCSRmatrix(model);
            laplacian.printMatrixData("Laplacian CSR matrix data:");
            return new LoadedGraph(model, matrix, laplacian);
        } catch (IOException ex)
        {
            System.out.println("[GraphController] Failed to load graph: " + ex.getMessage());
            JOptionPane.showMessageDialog(parent, "Failed to load graph: " + ex.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
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
