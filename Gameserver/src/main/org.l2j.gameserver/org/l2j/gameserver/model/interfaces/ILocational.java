package org.l2j.gameserver.model.interfaces;

import org.l2j.gameserver.enums.Position;

import static org.l2j.commons.util.Util.falseIfNullOrElse;
import static org.l2j.gameserver.util.MathUtil.calculateHeadingFrom;

/**
 * Object world location storage interface.
 *
 * @author xban1x
 */
public interface ILocational {
    /**
     * Gets the X coordinate of this object.
     *
     * @return the X coordinate
     */
    int getX();

    /**
     * Gets the Y coordinate of this object.
     *
     * @return the current Y coordinate
     */
    int getY();

    /**
     * Gets the Z coordinate of this object.
     *
     * @return the current Z coordinate
     */
    int getZ();

    /**
     * Gets the heading of this object.
     *
     * @return the current heading
     */
    int getHeading();

    /**
     * Gets this object's location.
     *
     * @return a {@link ILocational} object containing the current position of this object
     */
    ILocational getLocation();

    /**
     * @param to
     * @return the heading to the target specified
     */
    @Deprecated
    default int calculateHeadingTo(ILocational to) {
        return calculateHeadingFrom(this, to);
    }

    /**
     * @param target
     * @return {@code true} if this location is in front of the target location based on the game's concept of position.
     */
    default boolean isInFrontOf(ILocational target) {
        return falseIfNullOrElse(target, t -> Position.getPosition(this, target) == Position.FRONT);
    }

    /**
     * @param target
     * @return {@code true} if this location is in one of the sides of the target location based on the game's concept of position.
     */
    default boolean isOnSideOf(ILocational target) {
        return falseIfNullOrElse(target, t -> Position.getPosition(this, target) == Position.SIDE);
    }

    /**
     * @param target
     * @return {@code true} if this location is behind the target location based on the game's concept of position.
     */
    default boolean isBehind(ILocational target) {
        return falseIfNullOrElse(target, t -> Position.getPosition(this, target) == Position.BACK);
    }
}
