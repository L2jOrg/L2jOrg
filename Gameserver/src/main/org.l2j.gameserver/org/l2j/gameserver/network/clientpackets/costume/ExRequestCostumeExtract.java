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
package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.commons.util.StreamUtil;
import org.l2j.gameserver.data.database.data.CostumeData;
import org.l2j.gameserver.engine.costume.Costume;
import org.l2j.gameserver.engine.costume.CostumeEngine;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.costume.ExCostumeExtract;
import org.l2j.gameserver.network.serverpackets.costume.ExSendCostumeList;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.*;

/**
 * @author JoeAlisson
 */
public class ExRequestCostumeExtract extends ClientPacket {

    private int id;
    private long amount;

    @Override
    protected void readImpl() throws Exception {
        readShort(); // data size
        id = readInt();
        amount = readLong();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        var playerCostume = player.getCostume(id);
        Costume costume;

        var costumeEngine = CostumeEngine.getInstance();
        if(canExtract(player, playerCostume) && consumeItemsCost(player, costume = costumeEngine.getCostume(id))) {
            playerCostume.reduceCount(amount);
            client.sendPacket(new ExSendCostumeList(playerCostume));
            client.sendPacket(ExCostumeExtract.success(playerCostume, costume.extractItem(), amount));
            player.addItem("Extract", costume.extractItem(), amount, null, true);

            if(playerCostume.getAmount() <= 0) {
                player.removeCostume(id);
                player.removeSkill(costume.skill());
                costumeEngine.checkCostumeCollection(player, id);
            }
        } else {
            client.sendPacket(ExCostumeExtract.failed(id));
        }
    }

    private boolean canExtract(Player player, CostumeData costume) {
        if(isNull(costume) || costume.getAmount() < amount) {
            player.sendPacket(THIS_TRANSFORMATION_CANNOT_BE_EXTRACTED);
            return false;
        } else if(!player.isInventoryUnder90(true)) {
            player.sendPacket(NOT_ENOUGH_SPACE_IN_THE_INVENTORY_PLEASE_MAKE_MORE_ROOM_AND_TRY_AGAIN);
            return false;
        }
        return CostumeEngine.getInstance().checkCostumeAction(player);
    }

    private boolean consumeItemsCost(Player player, Costume costume) {
        var extractCost = costume.extractCost();
        var inventory = player.getInventory();
        try {
            var blockItems = StreamUtil.collectToSet(extractCost.stream().mapToInt(ItemHolder::getId));
            inventory.setInventoryBlock(blockItems, InventoryBlockType.BLACKLIST);
            for (ItemHolder cost : extractCost) {
                if(inventory.getInventoryItemCount(cost.getId(), -1) < cost.getCount() * amount) {
                    player.sendPacket(NOT_ENOUGH_MATERIAL_TO_EXTRACT);
                    return false;
                }
            }
            for (ItemHolder itemHolder : extractCost) {
                player.destroyItemByItemId("Consume", itemHolder.getId(), itemHolder.getCount(), player, true);
            }
        } finally {
            inventory.unblock();
        }
        return true;
    }
}
