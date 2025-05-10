package graphdivider.gui.navigation;

/**
 * The Navigator interface defines a contract for switching between registered screens in the application.
 */
public interface Navigator
{
    /**
     * Switches the currently displayed screen to the one associated with the given screenId.
     *
     * @param screenId a unique identifier for the target screen
     */
    void show(String screenId);
}
