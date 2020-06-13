package org.l2j.gameserver.model.item.type;

/**
 * Created for allow comparing different item types
 *
 * @author DS
 */
public interface ItemType {
    int mask();

    default boolean isRanged() {
        return false;
    }
}