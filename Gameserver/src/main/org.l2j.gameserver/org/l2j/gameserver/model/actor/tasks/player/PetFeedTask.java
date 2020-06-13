/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Task dedicated for feeding player's pet.
 *
 * @author UnAfraid
 */
public class PetFeedTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetFeedTask.class);

    private final Player _player;

    public PetFeedTask(Player player) {
        _player = player;
    }

    @Override
    public void run() {
        if (_player != null) {
            try {
                if (!_player.isMounted() || (_player.getMountNpcId() == 0) || (_player.getPetData(_player.getMountNpcId()) == null)) {
                    _player.stopFeed();
                    return;
                }

                if (_player.getCurrentFeed() > _player.getFeedConsume()) {
                    // eat
                    _player.setCurrentFeed(_player.getCurrentFeed() - _player.getFeedConsume());
                } else {
                    // go back to pet control item, or simply said, unsummon it
                    _player.setCurrentFeed(0);
                    _player.stopFeed();
                    _player.dismount();
                    _player.sendPacket(SystemMessageId.YOU_ARE_OUT_OF_FEED_MOUNT_STATUS_CANCELED);
                }

                final List<Integer> foodIds = _player.getPetData(_player.getMountNpcId()).getFood();
                if (foodIds.isEmpty()) {
                    return;
                }
                Item food = null;
                for (int id : foodIds) {
                    // TODO: possibly pet inv?
                    food = _player.getInventory().getItemByItemId(id);
                    if (food != null) {
                        break;
                    }
                }

                if ((food != null) && _player.isHungry()) {
                    final IItemHandler handler = ItemHandler.getInstance().getHandler(food.getEtcItem());
                    if (handler != null) {
                        handler.useItem(_player, food, false);
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_WAS_HUNGRY_SO_IT_ATE_S1);
                        sm.addItemName(food.getId());
                        _player.sendPacket(sm);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Mounted Pet [NpcId: " + _player.getMountNpcId() + "] a feed task error has occurred", e);
            }
        }
    }
}
