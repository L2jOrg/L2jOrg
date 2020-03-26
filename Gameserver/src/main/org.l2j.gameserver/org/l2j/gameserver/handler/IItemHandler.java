package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.items.instance.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mother class of all Item Handlers.
 */
public interface IItemHandler {
    Logger LOGGER = LoggerFactory.getLogger(IItemHandler.class.getName());

    /**
     * Launch task associated to the item.
     *
     * @param playable the non-NPC character using the item
     * @param item     Item designating the item to use
     * @param forceUse ctrl hold on item use
     * @return {@code true} if the item all conditions are met and the item is used, {@code false} otherwise.
     */
    boolean useItem(Playable playable, Item item, boolean forceUse);
}
