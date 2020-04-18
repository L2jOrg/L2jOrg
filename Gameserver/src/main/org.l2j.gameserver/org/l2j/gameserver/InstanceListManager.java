package org.l2j.gameserver;

/**
 * Interface for managers of list of instances.
 *
 * @author fordfrog
 */
public interface InstanceListManager {
    /**
     * Loads instances with their data from persistent format.<br>
     * This method has no side effect as calling methods of another instance manager.
     */
    void loadInstances();

    /**
     * For each loaded instance, updates references to related instances.
     */
    void updateReferences();

    /**
     * Activates instances so their setup is performed.
     */
    void activateInstances();
}
