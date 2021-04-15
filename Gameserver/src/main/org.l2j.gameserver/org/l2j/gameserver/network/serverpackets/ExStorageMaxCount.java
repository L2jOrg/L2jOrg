/*
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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.settings.CharacterSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

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

    public ExStorageMaxCount(Player player) {
        _inventory = player.getInventoryLimit();
        _warehouse = player.getWareHouseLimit();
        // _freight = Config.ALT_FREIGHT_SLOTS; // Removed with 152.
        _privateSell = player.getPrivateSellStoreLimit();
        _privateBuy = player.getPrivateBuyStoreLimit();

        var settings = getSettings(CharacterSettings.class);

        _clan = settings.clanMaxWarehouseSlot;
        _receipeD = player.getDwarfRecipeLimit();
        _recipe = player.getCommonRecipeLimit();
        _inventoryExtraSlots = (int) player.getStats().getValue(Stat.INVENTORY_NORMAL, 0);
        _inventoryQuestItems =  settings.maxSlotsQuestItem;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_STORAGE_MAX_COUNT, buffer );

        buffer.writeInt(_inventory);
        buffer.writeInt(_warehouse);
        // writeInt(_freight); // Removed with 152.
        buffer.writeInt(_clan);
        buffer.writeInt(_privateSell);
        buffer.writeInt(_privateBuy);
        buffer.writeInt(_receipeD);
        buffer.writeInt(_recipe);
        buffer.writeInt(_inventoryExtraSlots); // Belt inventory slots increase count
        buffer.writeInt(_inventoryQuestItems);
        buffer.writeInt(40); // TODO: Find me!
        buffer.writeInt(40); // TODO: Find me!
        buffer.writeInt(0x64); // Artifact slots (Fixed)
    }

}
