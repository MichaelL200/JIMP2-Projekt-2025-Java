package graphdivider.controller;

import graphdivider.model.CSRmatrix;
import graphdivider.model.GraphLoader;
import graphdivider.model.GraphModel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Controller for handling graph-related actions and business logic.
 * Decouples file loading and graph operations from the view (Frame).
 * Extend this class to add partitioning, saving, and other business logic.
 */
public final class GraphController
{
    /**
     * Loads a graph from the given file and returns the model, matrix, and Laplacian matrix.
     * Handles error dialogs and returns null if loading fails.
     *
     * @param parent the parent component for dialogs (can be null)
     * @param file   the file to load
     * @return a LoadedGraph object containing the model, matrix, and Laplacian matrix, or null if failed
     */
    public LoadedGraph loadGraphFromFile(JFrame parent, File file)
    {
        try
        {
            GraphModel model = GraphLoader.loadFromFile(file);
            CSRmatrix matrix = GraphLoader.toCSRmatrix(model);
            CSRmatrix laplacian = GraphLoader.toLaplacianCSRmatrix(model);
            return new LoadedGraph(model, matrix, laplacian);
        } catch (IOException ex)
        {
            // Show error dialog if loading fails
            JOptionPane.showMessageDialog(parent, "Failed to load graph: " + ex.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Data holder for loaded graph model, matrix, and Laplacian matrix.
     * Used to pass model, matrix, and Laplacian matrix to the view or other components.
     */
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

    // Future: Add methods for partitioning, saving, etc.
}
