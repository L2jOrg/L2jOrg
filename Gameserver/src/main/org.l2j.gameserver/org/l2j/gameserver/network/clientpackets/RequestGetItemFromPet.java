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

import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/29 23:15:33 $
 */
public final class RequestGetItemFromPet extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGetItemFromPet.class);
    private int _objectId;
    private long _amount;
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    public void readImpl() {
        _objectId = readInt();
        _amount = readLong();
        _unknown = readInt(); // = 0 for most trades
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if ((_amount <= 0) || (player == null) || !player.hasPet()) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("getfrompet")) {
            player.sendMessage("You get items from pet too fast.");
            return;
        }

        if (player.hasItemRequest()) {
            return;
        }

        final Pet pet = player.getPet();
        final Item item = pet.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            return;
        }

        if (_amount > item.getCount()) {
            GameUtils.handleIllegalPlayerAction(player, getClass().getSimpleName() + ": Character " + player.getName() + " of account " + player.getAccountName() + " tried to get item with oid " + _objectId + " from pet but has invalid count " + _amount + " item count: " + item.getCount());
            return;
        }

        final Item transferedItem = pet.transferItem("Transfer", _objectId, _amount, player.getInventory(), player, pet);
        if (transferedItem != null) {
            player.sendPacket(new PetItemList(pet.getInventory().getItems()));
        } else {
            LOGGER.warn("Invalid item transfer request: " + pet.getName() + "(pet) --> " + player.getName());
        }
    }
}
