package graphdivider.controller;

import graphdivider.model.GraphLoader;
import graphdivider.model.GraphPartitioner;
import graphdivider.view.ui.Graph;

import java.io.File;
import java.io.IOException;

public final class GraphController
{
    /**
     * Loads a text-based graph file and returns the loaded graph.
     * Throws IOException on failure.
     */
    public GraphLoader.LoadedGraph loadTextGraph(File selectedFile) throws IOException
    {
        return GraphLoader.loadGraphWithMatrices(selectedFile);
    }

    /**
     * Loads a partitioned text graph file and returns the loaded graph.
     * Throws IOException on failure.
     */
    public GraphLoader.LoadedGraph loadPartitionedTextGraph(File selectedFile) throws IOException
    {
        return GraphPartitioner.loadPartitionedTextGraph(selectedFile);
    }

    /**
     * Loads a partitioned binary graph file and returns the loaded graph.
     * Throws IOException on failure.
     */
    public GraphLoader.LoadedGraph loadPartitionedBinaryGraph(File selectedFile) throws IOException
    {
        return GraphPartitioner.loadPartitionedBinaryGraph(selectedFile);
    }

    /**
     * Loads a graph file of the specified type and displays it on the given graph panel.
     * Handles both loading and display logic.
     */
    public void loadAndDisplayGraph(File selectedFile, Object type, Graph graphPanel) throws IOException
    {
        GraphLoader.LoadedGraph loaded;
        if (type instanceof Enum<?> enumType) {
            switch (enumType.name()) {
                case "TEXT" -> loaded = loadTextGraph(selectedFile);
                case "PARTITIONED_TEXT" -> loaded = loadPartitionedTextGraph(selectedFile);
                case "PARTITIONED_BINARY" -> loaded = loadPartitionedBinaryGraph(selectedFile);
                default -> throw new IllegalArgumentException("Unknown graph load type: " + type);
            }
        } else {
            throw new IllegalArgumentException("Invalid graph load type: " + type);
        }
        graphPanel.displayGraph(loaded.model);
    }
}
