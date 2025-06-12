package graphdivider.view.ui;

import graphdivider.model.GraphModel;
import graphdivider.view.Theme;
import graphdivider.view.ui.graph.Edge;
import graphdivider.view.ui.graph.Vertex;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel for drawing the graph visualization.
 * Handles layout, drawing, and updating of vertices and edges.
 */
public final class Graph extends JPanel
{
    // All vertices (as components)
    private final List<Vertex> vertices = new ArrayList<>();
    // All edges (drawn manually)
    private final List<Edge> edges = new ArrayList<>();
    // Map: vertex index -> Vertex component
    private java.util.Map<Integer, Vertex> vertexIndexToComponent;
    // Reference to tool panel for enabling/disabling controls
    private final ToolPanel toolPanel;
    // Cached preferred size for layout optimization
    private Dimension cachedPreferredSize = null;

    /**
     * Constructs the Graph panel and sets up theme listeners.
     * 
     * @param toolPanel Reference to the ToolPanel for control interaction.
     */
    public Graph(ToolPanel toolPanel)
    {
        this.toolPanel = toolPanel;
        setLayout(null);      // Absolute positioning
        setOpaque(false);     // Transparent background

        // Update edge colors on theme change
        Theme.addThemeListener(
            () ->
            {
                for (Edge edge : edges)
                {
                    edge.updateEdgeColor();
                }
                repaint();
            }
        );
    }

    /**
     * Displays the graph from the given model, clearing any previous content.
     * Sets up vertices and edges, updates layout, and enables partition button.
     * 
     * @param model The GraphModel to visualize.
     */
    public void displayGraph(GraphModel model)
    {
        clearGraph();
        setupVertices(model);
        setupEdges(model);
        updatePreferredSize();
        revalidate();
        repaint();
        toolPanel.setPartitionButtonEnabled(true);

        updateTooltips();
    }

    /**
     * Updates tooltips for all vertices based on their neighbors.
     * Should be called after graph or cluster changes.
     */
    public void updateTooltips()
    {
        // For each vertex, collect its neighbors from the edges
        for (Vertex v : vertices)
        {
            List<Vertex> neighbors = edges.stream()
                    .filter(e -> e.getVertex1() == v || e.getVertex2() == v)
                    .map(e -> e.getVertex1() == v ? e.getVertex2() : e.getVertex1())
                    .collect(Collectors.toList());
            v.setNeighborsAndUpdateTooltip(neighbors);
        }
    }

    /**
     * Removes all vertices and edges from the panel.
     * Resets internal state and layout.
     */
    public void clearGraph()
    {
        for (Vertex v : vertices) remove(v);
        vertices.clear();
        for (Edge e : edges) e.dispose();
        edges.clear();
        vertexIndexToComponent = null;
        cachedPreferredSize = null;
        setPreferredSize(new Dimension(0, 0));
        revalidate();
        repaint();
    }

    /**
     * Gets all vertices as an array.
     * 
     * @return Array of Vertex components.
     */
    public Vertex[] getVertices()
    {
        return this.vertices.toArray(new Vertex[0]);
    }

    /**
     * Gets all edges as a list.
     * 
     * @return List of Edge objects.
     */
    public java.util.List<Edge> getEdges()
    {
        return this.edges;
    }

    /**
     * Updates vertex colors/clusters based on the given cluster assignments.
     * 
     * @param clusters Array of cluster indices for each vertex.
     * @throws IllegalArgumentException if input arrays are invalid.
     */
    public void updateClusters(int[] clusters)
    {
        Vertex[] vertexArray = getVertices();
        if (vertexArray == null || clusters == null || vertexArray.length != clusters.length)
            throw new IllegalArgumentException("Vertices and clusters must be non-null and of the same length.");
        graphdivider.view.ui.graph.GraphColoring.colorVertices(vertexArray, clusters, edges);
        repaint();

        updateTooltips();
    }

    /**
     * Gets the preferred size of the panel, using a cached value if available.
     * 
     * @return Preferred Dimension for the panel.
     */
    @Override
    public Dimension getPreferredSize()
    {
        return cachedPreferredSize != null ? cachedPreferredSize : super.getPreferredSize();
    }

    /**
     * Paints all edges on the panel.
     * Vertices are painted by their own components.
     * 
     * @param g Graphics context.
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Iterate over a copy to avoid ConcurrentModificationException
        List<Edge> edgesCopy = new ArrayList<>(edges);
        for (Edge edge : edgesCopy)
        {
            edge.draw(g2);
        }
        g2.dispose();
    }

    // --- Private helpers ---

    /**
     * Updates the preferred size of the panel to fit all vertices.
     * Adds a margin for better appearance.
     */
    private void updatePreferredSize()
    {
        if (vertices.isEmpty())
        {
            cachedPreferredSize = new Dimension(0, 0);
            setPreferredSize(cachedPreferredSize);
            return;
        }
        int maxX = 0, maxY = 0;
        for (Vertex v : vertices)
        {
            Rectangle bounds = v.getBounds();
            maxX = Math.max(maxX, bounds.x + bounds.width);
            maxY = Math.max(maxY, bounds.y + bounds.height);
        }
        // Add margin
        int margin = 40;
        cachedPreferredSize = new Dimension(maxX + margin, maxY + margin);
        setPreferredSize(cachedPreferredSize);
    }

    /**
     * Finds the row index for a given vertex index.
     * 
     * @param vertexIndex Index of the vertex.
     * @param rowStartIndices Array of row start indices.
     * @param vertexCount Total number of vertices.
     * @return Row index, or -1 if not found.
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
        return -1;
    }

    /**
     * Maps actual row indices to visual row indices (skips empty rows).
     * 
     * @param usedRows Set of used row indices.
     * @param rowStartIndices Array of row start indices.
     * @return Map from row index to visual row index.
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
     * Sets up vertices from the graph model and adds them to the panel.
     * 
     * @param model The GraphModel containing vertex data.
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
        int margin = 10;
        int rowSpacing = 60;

        // Find used rows
        java.util.Set<Integer> usedRows = new java.util.HashSet<>();
        for (int i = 0; i < vertexCount; i++)
        {
            int row = findRowForVertex(i, rowStartIndices, vertexCount);
            usedRows.add(row);
        }

        // Map row index to visual row index
        java.util.Map<Integer, Integer> rowToVisualRow = buildRowToVisualRow(usedRows, rowStartIndices);

        // Map: vertex index -> Vertex
        java.util.Map<Integer, Vertex> vertexIndexToComponent = new java.util.HashMap<>();

        // Place each vertex
        for (int i = 0; i < vertexCount; i++)
        {
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

        // Store mapping for edges
        this.vertexIndexToComponent = vertexIndexToComponent;
    }

    /**
     * Sets up edges from the graph model and adds them to the panel.
     * Avoids duplicate edges for undirected graphs.
     * 
     * @param model The GraphModel containing edge data.
     */
    private void setupEdges(GraphModel model)
    {
        if (this.vertexIndexToComponent == null) return;

        int vertexCount = model.getRowPositions().length;
        int[] adjacencyList = model.getAdjacencyList();
        int[] adjacencyPointers = model.getAdjacencyPointers();

        // Avoid duplicate edges (undirected)
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
                if (v1 == v2) continue;
                String key = v1 + "-" + v2;
                if (!edgeSet.contains(key)
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
}