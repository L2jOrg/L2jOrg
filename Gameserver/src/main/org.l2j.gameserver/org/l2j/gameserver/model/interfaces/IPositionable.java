package org.l2j.gameserver.model.interfaces;

import org.l2j.gameserver.model.Location;

/**
 * Object world location storage and update interface.
 *
 * @author Zoey76
 */
public interface IPositionable extends ILocational {
    /**
     * Sets all three coordinates of this object.
     *
     * @param x the new X coordinate
     * @param y the new Y coordinate
     * @param z the new Z coordinate
     */
    void setXYZ(int x, int y, int z);

    /**
     * Sets all three coordinates of this object.
     *
     * @param loc the object whose coordinates to use
     */
    void setXYZ(ILocational loc);

    /**
     * Sets the heading of this object.
     *
     * @param heading the new heading
     */
    void setHeading(int heading);

    /**
     * Changes the location of this object.
     *
     * @param loc the new location
     */
    void setLocation(Location loc);
}