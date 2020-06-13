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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class ...
 *
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/29 23:15:33 $
 */
public final class RequestGiveItemToPet extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGiveItemToPet.class);
    private int _objectId;
    private long _amount;

    @Override
    public void readImpl() {
        _objectId = readInt();
        _amount = readLong();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if ((_amount <= 0) || (player == null) || !player.hasPet()) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("giveitemtopet")) {
            player.sendMessage("You are giving items to pet too fast.");
            return;
        }

        if (player.hasItemRequest()) {
            return;
        }

        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (player.getReputation() < 0)) {
            return;
        }

        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendMessage("You cannot exchange items while trading.");
            return;
        }

        final Item item = player.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            return;
        }

        if (_amount > item.getCount()) {
            GameUtils.handleIllegalPlayerAction(player, getClass().getSimpleName() + ": Character " + player.getName() + " of account " + player.getAccountName() + " tried to get item with oid " + _objectId + " from pet but has invalid count " + _amount + " item count: " + item.getCount());
            return;
        }

        if (item.isAugmented()) {
            return;
        }

        if (item.isHeroItem() || !item.isDropable() || !item.isDestroyable() || !item.isTradeable()) {
            player.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return;
        }

        final Pet pet = player.getPet();
        if (pet.isDead()) {
            player.sendPacket(SystemMessageId.YOUR_PET_IS_DEAD_AND_ANY_ATTEMPT_YOU_MAKE_TO_GIVE_IT_SOMETHING_GOES_UNRECOGNIZED);
            return;
        }

        var inventory = pet.getInventory();

        if (!inventory.validateCapacity(item) || !inventory.validateWeight(item, _amount)) {
            player.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
            return;
        }

        final Item transferedItem = player.transferItem("Transfer", _objectId, _amount, pet.getInventory(), pet);
        if (transferedItem != null)
        {
            player.sendPacket(new PetItemList(inventory.getItems()));
        }
        else {
            LOGGER.warn("Invalid item transfer request: " + pet.getName() + "(pet) --> " + player.getName());
        }
    }
}
