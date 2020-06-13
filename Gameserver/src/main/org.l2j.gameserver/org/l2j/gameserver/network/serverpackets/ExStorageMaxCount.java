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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author -Wooden-, KenM
 */
public class ExStorageMaxCount extends ServerPacket {
    private final int _inventory;
    private final int _warehouse;
    // private final int _freight; // Removed with 152.
    private final int _clan;
    private final int _privateSell;
    private final int _privateBuy;
    private final int _receipeD;
    private final int _recipe;
    private final int _inventoryExtraSlots;
    private final int _inventoryQuestItems;

    public ExStorageMaxCount(Player activeChar) {
        _inventory = activeChar.getInventoryLimit();
        _warehouse = activeChar.getWareHouseLimit();
        // _freight = Config.ALT_FREIGHT_SLOTS; // Removed with 152.
        _privateSell = activeChar.getPrivateSellStoreLimit();
        _privateBuy = activeChar.getPrivateBuyStoreLimit();
        _clan = Config.WAREHOUSE_SLOTS_CLAN;
        _receipeD = activeChar.getDwarfRecipeLimit();
        _recipe = activeChar.getCommonRecipeLimit();
        _inventoryExtraSlots = (int) activeChar.getStats().getValue(Stat.INVENTORY_NORMAL, 0);
        _inventoryQuestItems = Config.INVENTORY_MAXIMUM_QUEST_ITEMS;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_STORAGE_MAX_COUNT);

        writeInt(_inventory);
        writeInt(_warehouse);
        // writeInt(_freight); // Removed with 152.
        writeInt(_clan);
        writeInt(_privateSell);
        writeInt(_privateBuy);
        writeInt(_receipeD);
        writeInt(_recipe);
        writeInt(_inventoryExtraSlots); // Belt inventory slots increase count
        writeInt(_inventoryQuestItems);
        writeInt(40); // TODO: Find me!
        writeInt(40); // TODO: Find me!
        writeInt(0x64); // Artifact slots (Fixed)
    }

}
