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

// Panel for drawing the graph visualization
public final class Graph extends JPanel
{
    // All vertices (as components)
    private final List<Vertex> vertices = new ArrayList<>();
    // All edges (drawn manually)
    private final List<Edge> edges = new ArrayList<>();
    // Map: vertex index -> Vertex component
    private java.util.Map<Integer, Vertex> vertexIndexToComponent;
    // Reference to tool panel
    private final ToolPanel toolPanel;
    // Cached preferred size
    private Dimension cachedPreferredSize = null;

    // Setup panel, listen for theme changes
    public Graph(ToolPanel toolPanel)
    {
        this.toolPanel = toolPanel;
        setLayout(null);      // Absolute positioning
        setOpaque(false);     // Transparent background

        // Update edge colors on theme change
        Theme.addThemeListener(() ->
        {
            for (Edge edge : edges)
            {
                edge.updateEdgeColor();
            }
            repaint();
        });
    }

    // Show graph from model, clear previous
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

    // Make this method public
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

    // Remove all vertices and edges
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

    // Get all vertices as array
    public Vertex[] getVertices()
    {
        return this.vertices.toArray(new Vertex[0]);
    }

    // Get all edges as list
    public java.util.List<Edge> getEdges()
    {
        return this.edges;
    }

    // Update vertex colors/clusters
    public void updateClusters(int[] clusters)
    {
        Vertex[] vertexArray = getVertices();
        if (vertexArray == null || clusters == null || vertexArray.length != clusters.length)
            throw new IllegalArgumentException("Vertices and clusters must be non-null and of the same length.");
        graphdivider.view.ui.graph.GraphColoring.colorVertices(vertexArray, clusters, edges);
        repaint();

        updateTooltips();
    }

    // Use cached preferred size
    @Override
    public Dimension getPreferredSize()
    {
        return cachedPreferredSize != null ? cachedPreferredSize : super.getPreferredSize();
    }

    // Draw all edges (vertices are drawn by their own components)
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

    // Update preferred size to fit all vertices
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

    // Find row for a vertex index
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

    // Map actual row indices to visual row indices (skip empty)
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

    // Setup vertices from model
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

    // Setup edges from model
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