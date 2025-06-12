package graphdivider.view;

import graphdivider.controller.GraphController;
import graphdivider.view.ui.*;
import graphdivider.view.ui.MenuBar;

import javax.swing.*;
import java.awt.*;

// Main window for the Graph Divider tool.
public final class Frame extends JFrame
{
    // Panels and UI components
    private final Graph graphPanel;
    private final ToolPanel toolPanel;
    private final PartitionPanel partitionPanel;
    private final MenuBar menuBar;

    // Theme and controller state
    private GraphController controller;
    private java.io.File currentFile = null; // Track current file for title

    public Frame()
    {
        setTitle("Graph Divider");

        // Set minimum resizable frame size
        setMinimumSize(new Dimension(900, 600));

        // Setup menu bar
        menuBar = new MenuBar();
        setJMenuBar(menuBar);

        // Setup tool and partition panels (left side)
        toolPanel = new ToolPanel();
        partitionPanel = new PartitionPanel();

        // Use JSplitPane for dynamic 66/33 split, not resizable by user
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toolPanel, partitionPanel);
        splitPane.setResizeWeight(0.66);
        splitPane.setDividerSize(0); // Hide divider, prevent resizing
        splitPane.setEnabled(false); // Prevent user interaction
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);

        // Set minimum/preferred sizes for better resizing
        toolPanel.setMinimumSize(new Dimension(220, 50));
        partitionPanel.setMinimumSize(new Dimension(220, 50));
        toolPanel.setPreferredSize(new Dimension(220, 200));
        partitionPanel.setPreferredSize(new Dimension(220, 200));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.WEST);

        // Setup graph panel with scrollbars (center)
        graphPanel = new Graph(toolPanel);
        JScrollPane scrollPane = new JScrollPane(graphPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBar(new graphdivider.view.ui.ScrollBar(JScrollBar.VERTICAL));
        scrollPane.setHorizontalScrollBar(new graphdivider.view.ui.ScrollBar(JScrollBar.HORIZONTAL));
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(1800, 900));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Set partition panel to unknown at startup
        partitionPanel.setUnknown();

        // Register language change listeners for panels and tooltips
        graphdivider.view.Language.addLanguageChangeListener(() ->
        {
            toolPanel.updateTexts();
            partitionPanel.updateTexts();
            graphPanel.updateTooltips();
        });
    }

    // Set the controller (must be called before using controller)
    public void setController(GraphController controller)
    {
        this.controller = controller;
    }

    // Set window title based on file
    public void setWindowTitleForFile(java.io.File file)
    {
        this.currentFile = file;
        if (file != null)
        {
            System.out.println("[Frame] Setting window title for file: " + file.getName());
            setTitle("Graph Divider - " + file.getName());

        } else
        {
            setTitle("Graph Divider");
        }
    }

    // Add this method to allow updating the window title after language change
    public void updateWindowTitle()
    {
        setWindowTitleForFile(currentFile);
    }

    // Delegate: Handle loading a text graph file
    public void handleLoadTextGraph()
    {
        if (controller != null) {
            controller.loadTextGraph(this);
        }
    }

    // Delegate: Handle loading a partitioned text graph
    public void handleLoadPartitionedTextGraph()
    {
        if (controller != null) {
            controller.loadPartitionedTextGraph(this);
        }
    }

    // Delegate: Handle loading a partitioned binary graph
    public void handleLoadPartitionedBinaryGraph()
    {
        if (controller != null) {
            controller.loadPartitionedBinaryGraph(this);
        }
    }

    // Update window icon for current theme
    public void updateWindowIcon()
    {
        setIconImage(Theme.loadSystemAwareWindowIcon());
    }

    // Getters for panels and menu bar
    public ToolPanel getToolPanel() { return toolPanel; }
    public PartitionPanel getPartitionPanel() { return partitionPanel; }
    public MenuBar getAppMenuBar() { return menuBar; }
    public Graph getGraphPanel() { return graphPanel; }

    // Update tool panel's max partitions and spinner value
    public void updateToolPanelPartitions(int maxPartitions, int spinnerValue)
    {
        getToolPanel().setMaxPartitionCount(maxPartitions);
        getToolPanel().setPartitionCountSpinnerValue(spinnerValue);

        // Set minimum margin based on maxPartitions (example formula)
        int minMargin = Math.max(0, 100 / maxPartitions);
        getToolPanel().setMinPartitionMargin(minMargin);
        // Set spinner value to minMargin to ensure it's valid
        getToolPanel().getPartitionMarginSpinner().setValue(minMargin);

        // Set partition panel to unknown after choosing a file
        getPartitionPanel().setUnknown();
    }

    // Enable/disable tool panel controls
    public void setToolPanelEnabled(boolean enabled)
    {
        getToolPanel().setPartitionButtonEnabled(enabled);
        getToolPanel().getPartitionCountSpinner().setEnabled(enabled);
        getToolPanel().getPartitionMarginSpinner().setEnabled(enabled);
    }

    // Update partition panel with new values
    public void updatePartitionPanel(int edgesCut, double marginKept)
    {
        getPartitionPanel().setEdgesCut(edgesCut);
        getPartitionPanel().setMarginKept(marginKept);
    }

    // Update the menu language
    public void updateMenuLanguage()
    {
        // Only refresh the existing menuBar, do not call createMenuBar()
        if (menuBar != null)
        {
            menuBar.updateMenuTexts();
            setJMenuBar(menuBar);
            revalidate();
            repaint();
        }
        toolPanel.updateTexts();
        partitionPanel.updateTexts();
        graphPanel.updateTooltips();
    }
}