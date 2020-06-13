/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.item.instance.Item;
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
