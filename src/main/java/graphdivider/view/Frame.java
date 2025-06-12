package graphdivider.view;

import graphdivider.controller.GraphController;
import graphdivider.view.ui.*;
import graphdivider.view.ui.MenuBar;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window for the Graph Divider tool.
 * Manages layout, panels, and UI updates.
 */
public final class Frame extends JFrame
{
    // Central graph visualization panel (center)
    private final Graph graphPanel;
    // Panel with partitioning controls (left)
    private final ToolPanel toolPanel;
    // Panel showing partition results (left)
    private final PartitionPanel partitionPanel;
    // Application menu bar (top)
    private final MenuBar menuBar;

    // Controller for graph logic and actions
    private GraphController controller;
    // Track current file for window title
    private java.io.File currentFile = null;

    /**
     * Constructs the main window and initializes all UI components.
     * Sets up layout, panels, menu bar, and language change listeners.
     */
    public Frame()
    {
        setTitle("Graph Divider");

        // Set minimum resizable frame size for usability
        setMinimumSize(new Dimension(900, 600));

        // Setup menu bar
        menuBar = new MenuBar();
        setJMenuBar(menuBar);

        // Setup tool and partition panels (left side)
        toolPanel = new ToolPanel();
        partitionPanel = new PartitionPanel();

        // Use JSplitPane for vertical split (tool/partition panels)
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, 
            toolPanel, 
            partitionPanel
        );
        splitPane.setResizeWeight(0.66); // 66% tool, 33% partition
        splitPane.setDividerSize(0);     // Hide divider, prevent resizing
        splitPane.setEnabled(false);     // Prevent user interaction
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
        JScrollPane scrollPane = new JScrollPane(
            graphPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setVerticalScrollBar(new graphdivider.view.ui.ScrollBar(JScrollBar.VERTICAL));
        scrollPane.setHorizontalScrollBar(new graphdivider.view.ui.ScrollBar(JScrollBar.HORIZONTAL));
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(1800, 900));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Set partition panel to unknown at startup (no data)
        partitionPanel.setUnknown();

        /*
         * Listen for language changes and update all UI texts/tooltips.
         * Ensures the UI is always in sync with the selected language.
         */
        graphdivider.view.Language.addLanguageChangeListener(
            () ->
            {
                toolPanel.updateTexts();
                partitionPanel.updateTexts();
                graphPanel.updateTooltips();
            }
        );
    }

    /**
     * Sets the controller for this frame.
     * Must be called before using controller-dependent features.
     * 
     * @param controller The GraphController to use.
     */
    public void setController(GraphController controller)
    {
        this.controller = controller;
    }

    /**
     * Sets the window title based on the provided file.
     * Shows the filename in the title bar.
     * 
     * @param file The file to display in the window title, or null for default title.
     */
    public void setWindowTitleForFile(java.io.File file)
    {
        this.currentFile = file;
        if (file != null)
        {
            System.out.println("[Frame] Setting window title for file: " + file.getName());
            setTitle("Graph Divider - " + file.getName());
        } 
        else
        {
            setTitle("Graph Divider");
        }
    }

    /**
     * Updates the window title after a language or file change.
     */
    public void updateWindowTitle()
    {
        setWindowTitleForFile(currentFile);
    }

    /**
     * Handles loading a text graph file.
     * Delegates to the controller.
     * 
     * @throws IllegalStateException if controller is not set
     */
    public void handleLoadTextGraph()
    {
        if (controller != null) 
        {
            controller.loadTextGraph(this);
        }
        // else: do nothing if controller is not set
    }

    /**
     * Handles loading a partitioned text graph file.
     * Delegates to the controller.
     * 
     * @throws IllegalStateException if controller is not set
     */
    public void handleLoadPartitionedTextGraph()
    {
        if (controller != null) 
        {
            controller.loadPartitionedTextGraph(this);
        }
        // else: do nothing if controller is not set
    }

    /**
     * Handles loading a partitioned binary graph file.
     * Delegates to the controller.
     * 
     * @throws IllegalStateException if controller is not set
     */
    public void handleLoadPartitionedBinaryGraph()
    {
        if (controller != null) 
        {
            controller.loadPartitionedBinaryGraph(this);
        }
        // else: do nothing if controller is not set
    }

    /**
     * Updates the window icon for the current theme (light/dark).
     */
    public void updateWindowIcon()
    {
        setIconImage(Theme.loadSystemAwareWindowIcon());
    }

    // --- Getters for panels and menu bar ---

    /**
     * Gets the tool panel (partitioning controls).
     * 
     * @return The tool panel.
     */
    public ToolPanel getToolPanel() 
    { 
        return toolPanel; 
    }

    /**
     * Gets the partition panel (partition results).
     * 
     * @return The partition panel.
     */
    public PartitionPanel getPartitionPanel() 
    { 
        return partitionPanel; 
    }

    /**
     * Gets the application menu bar.
     * 
     * @return The menu bar.
     */
    public MenuBar getAppMenuBar() 
    { 
        return menuBar; 
    }

    /**
     * Gets the central graph visualization panel.
     * 
     * @return The graph panel.
     */
    public Graph getGraphPanel() 
    { 
        return graphPanel; 
    }

    /**
     * Updates tool panel's max partitions and spinner value.
     * Also updates minimum margin and resets partition panel.
     * 
     * @param maxPartitions Maximum allowed partitions.
     * @param spinnerValue Value to set for the partition spinner.
     */
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

    /**
     * Enables or disables tool panel controls (partitioning).
     * 
     * @param enabled True to enable controls, false to disable.
     */
    public void setToolPanelEnabled(boolean enabled)
    {
        getToolPanel().setPartitionButtonEnabled(enabled);
        getToolPanel().getPartitionCountSpinner().setEnabled(enabled);
        getToolPanel().getPartitionMarginSpinner().setEnabled(enabled);
    }

    /**
     * Updates partition panel with new values (edges cut, margin kept).
     * Called after partitioning is performed.
     * 
     * @param edgesCut Number of edges cut by the partition.
     * @param marginKept Margin kept by the partition.
     */
    public void updatePartitionPanel(int edgesCut, double marginKept)
    {
        getPartitionPanel().setEdgesCut(edgesCut);
        getPartitionPanel().setMarginKept(marginKept);
    }

    /**
     * Updates all UI texts/tooltips for the current language.
     * Called after language change to refresh all visible texts.
     */
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