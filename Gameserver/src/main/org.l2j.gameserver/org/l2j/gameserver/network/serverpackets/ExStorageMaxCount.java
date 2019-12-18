package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

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
        writeId(ServerPacketId.EX_STORAGE_MAX_COUNT);

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
