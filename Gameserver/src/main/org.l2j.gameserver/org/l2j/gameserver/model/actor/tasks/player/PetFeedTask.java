/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task dedicated for feeding player's pet.
 *
 * @author UnAfraid
 */
public class PetFeedTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetFeedTask.class);

    private final Player player;

    public PetFeedTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player != null) {
            try {
                if (!player.isMounted() || (player.getMountNpcId() == 0) || (player.getPetData(player.getMountNpcId()) == null)) {
                    player.stopFeed();
                    return;
                }

                if (player.getCurrentFeed() > player.getFeedConsume()) {
                    // eat
                    player.setCurrentFeed(player.getCurrentFeed() - player.getFeedConsume());
                } else {
                    // go back to pet control item, or simply said, unsummon it
                    player.setCurrentFeed(0);
                    player.stopFeed();
                    player.dismount();
                    player.sendPacket(SystemMessageId.YOU_ARE_OUT_OF_FEED_MOUNT_STATUS_CANCELED);
                }

                var foodIds = player.getPetData(player.getMountNpcId()).getFood();
                if (foodIds.isEmpty()) {
                    return;
                }
                Item food = null;
                var it = foodIds.iterator();
                while(it.hasNext()) {
                    food = player.getInventory().getItemByItemId(it.nextInt());
                    if (food != null) {
                        break;
                    }
                }
                if ((food != null) && player.isHungry()) {
                    final IItemHandler handler = ItemHandler.getInstance().getHandler(food.getEtcItem());
                    if (handler != null) {
                        handler.useItem(player, food, false);
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_WAS_HUNGRY_SO_IT_ATE_S1);
                        sm.addItemName(food.getId());
                        player.sendPacket(sm);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Mounted Pet [NpcId: " + player.getMountNpcId() + "] a feed task error has occurred", e);
            }
        }
    }
}
