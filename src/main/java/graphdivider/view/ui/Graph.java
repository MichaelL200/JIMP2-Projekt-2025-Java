package graphdivider.view.ui;

import graphdivider.model.GraphModel;
import graphdivider.view.ui.graph.Edge;
import graphdivider.view.ui.graph.Vertex;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JPanel responsible for displaying the graph visualization.
 * Manages vertices and edges, and handles their rendering.
 * <p>
 * This class is intended to be the main drawing area for the graph.
 * Vertices are added as child components, while edges are drawn manually.
 */
public final class Graph extends JPanel
{
    // List of vertices in the graph (each is a component)
    private final List<Vertex> vertices = new ArrayList<>();
    // List of edges in the graph (drawn manually)
    private final List<Edge> edges = new ArrayList<>();
    // Store mapping for use between setupVertices and setupEdges
    private java.util.Map<Integer, Vertex> vertexIndexToComponent;
    // Reference to the ToolPanel instance
    private final ToolPanel toolPanel;

    /**
     * Constructs the graph panel.
     * Registers a theme change listener to update edge colors when the theme changes.
     */
    public Graph(ToolPanel toolPanel)
    {
        this.toolPanel = toolPanel;
        setLayout(null);      // Use absolute positioning for vertices
        setOpaque(false);     // Allow background to show through

        // Register a listener to update edge colors when the theme changes
        Theme.addThemeChangeListener(() ->
        {
            for (Edge edge : edges)
            {
                edge.updateEdgeColor();
            }
            repaint();
        });
    }

    /**
     * Displays the graph from the given GraphModel.
     * Clears any previous vertices and edges.
     *
     * @param model the GraphModel containing the vertices and edges
     */
    public void displayGraph(GraphModel model)
    {
        // Remove old vertices
        for (Vertex v : vertices)
        {
            remove(v);
        }
        vertices.clear();

        // Remove old edges
        for (Edge e : edges)
        {
            e.dispose();
        }
        edges.clear();

        setupVertices(model);
        setupEdges(model);

        revalidate();
        repaint();

        // Enable the divide button after the graph is displayed
        toolPanel.setDivideButtonEnabled(true);
    }

    /**
     * Helper method to find the row index for a given vertex index.
     */
    private int findRowForVertex(int vertexIndex, int[] rowStartIndices, int vertexCount)
    {
        for (int r = 0; r < rowStartIndices.length; r++)
        {
            int start = rowStartIndices[r];
            int end = (r + 1 < rowStartIndices.length) ? rowStartIndices[r + 1] : vertexCount;
            if (vertexIndex >= start && vertexIndex < end)
            {
                return r;
            }
        }
        return -1; // Not found
    }

    /**
     * Helper method to build a mapping from actual row indices to visual row indices (skipping empty rows).
     */
    private java.util.Map<Integer, Integer> buildRowToVisualRow(java.util.Set<Integer> usedRows, int[] rowStartIndices)
    {
        java.util.Map<Integer, Integer> rowToVisualRow = new java.util.HashMap<>();
        int visualRow = 0;
        for (int r = 0; r < rowStartIndices.length; r++)
        {
            if (usedRows.contains(r))
            {
                rowToVisualRow.put(r, visualRow++);
            }
        }
        return rowToVisualRow;
    }

    /**
     * Sets up vertices from the given GraphModel.
     * Clears and adds new Vertex components.
     */
    private void setupVertices(GraphModel model)
    {
        int vertexCount = model.getRowPositions().length;
        int[] rowStartIndices = model.getRowStartIndices();
        int[] rowPositions = model.getRowPositions();

        if (vertexCount == 0)
        {
            return;
        }

        int vertexDiameter = 40;
        int spacing = 20;
        int margin = 10; // left margin
        int rowSpacing = 60; // vertical space between rows

        // Determine which rows are actually used (contain at least one vertex)
        java.util.Set<Integer> usedRows = new java.util.HashSet<>();
        for (int i = 0; i < vertexCount; i++)
        {
            // Find the row this vertex belongs to
            int row = findRowForVertex(i, rowStartIndices, vertexCount);
            usedRows.add(row);
        }

        // Map actual row indices to visual row indices (skip empty rows)
        java.util.Map<Integer, Integer> rowToVisualRow = buildRowToVisualRow(usedRows, rowStartIndices);

        // Map: vertex index -> Vertex component
        java.util.Map<Integer, Vertex> vertexIndexToComponent = new java.util.HashMap<>();

        // For each vertex, determine its row and column, using only non-empty rows
        for (int i = 0; i < vertexCount; i++)
        {
            // Find the row this vertex belongs to
            int row = findRowForVertex(i, rowStartIndices, vertexCount);
            int col = rowPositions[i];
            int visualRowIdx = rowToVisualRow.get(row);

            int x = margin + col * (vertexDiameter + spacing);
            int y = 20 + visualRowIdx * rowSpacing;

            Vertex vertex = new Vertex(i, Vertex.DEFAULT_BLUE, vertexDiameter);
            vertex.setBounds(x, y, vertexDiameter, vertexDiameter);
            vertices.add(vertex);
            add(vertex);
            vertexIndexToComponent.put(i, vertex);
        }

        // Store the mapping for use in setupEdges
        this.vertexIndexToComponent = vertexIndexToComponent;
    }

    /**
     * Sets up edges from the given GraphModel.
     * Uses the mapping from vertex indices to Vertex components.
     */
    private void setupEdges(GraphModel model)
    {
        if (this.vertexIndexToComponent == null) return;

        int vertexCount = model.getRowPositions().length;
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();
        int[] rowStartIndices = model.getRowStartIndices();

        // To avoid duplicate edges (for undirected graphs), use a set of pairs (min, max)
        java.util.Set<String> edgeSet = new java.util.HashSet<>();
        for (int i = 0; i < adjacencyPointers.length; i++)
        {
            int start = adjacencyPointers[i];
            int end = (i + 1 < adjacencyPointers.length) ? adjacencyPointers[i + 1] : adjacencyList.length;
            int vertexIdx = adjacencyList[start];
            for (int j = start + 1; j < end; j++)
            {
                int neighborIdx = adjacencyList[j];
                if (neighborIdx < 0 || neighborIdx >= vertexCount) continue;
                int v1 = Math.min(vertexIdx, neighborIdx);
                int v2 = Math.max(vertexIdx, neighborIdx);
                String key = v1 + "-" + v2;
                if (!edgeSet.contains(key) && v1 != v2
                        && vertexIndexToComponent.containsKey(v1)
                        && vertexIndexToComponent.containsKey(v2))
                {
                    edgeSet.add(key);
                    Edge edge = new Edge(vertexIndexToComponent.get(v1), vertexIndexToComponent.get(v2));
                    edges.add(edge);
                }
            }
        }
    }

    /**
     * Paints the graph edges on the panel.
     * Vertices are painted by their own components (handled by Swing).
     *
     * @param g the Graphics context used for drawing
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Draw all edges before the vertices (so edges appear behind)
        for (Edge edge : edges)
        {
            edge.draw(g2);
        }
        g2.dispose();
    }
}
