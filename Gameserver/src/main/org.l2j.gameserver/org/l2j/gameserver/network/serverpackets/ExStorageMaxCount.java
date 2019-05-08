package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-, KenM
 */
public class ExStorageMaxCount extends IClientOutgoingPacket {
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

    public ExStorageMaxCount(L2PcInstance activeChar) {
        _inventory = activeChar.getInventoryLimit();
        _warehouse = activeChar.getWareHouseLimit();
        // _freight = Config.ALT_FREIGHT_SLOTS; // Removed with 152.
        _privateSell = activeChar.getPrivateSellStoreLimit();
        _privateBuy = activeChar.getPrivateBuyStoreLimit();
        _clan = Config.WAREHOUSE_SLOTS_CLAN;
        _receipeD = activeChar.getDwarfRecipeLimit();
        _recipe = activeChar.getCommonRecipeLimit();
        _inventoryExtraSlots = (int) activeChar.getStat().getValue(Stats.INVENTORY_NORMAL, 0);
        _inventoryQuestItems = Config.INVENTORY_MAXIMUM_QUEST_ITEMS;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_STORAGE_MAX_COUNT.writeId(packet);

        packet.putInt(_inventory);
        packet.putInt(_warehouse);
        // packet.putInt(_freight); // Removed with 152.
        packet.putInt(_clan);
        packet.putInt(_privateSell);
        packet.putInt(_privateBuy);
        packet.putInt(_receipeD);
        packet.putInt(_recipe);
        packet.putInt(_inventoryExtraSlots); // Belt inventory slots increase count
        packet.putInt(_inventoryQuestItems);
        packet.putInt(40); // TODO: Find me!
        packet.putInt(40); // TODO: Find me!
        packet.putInt(0x64); // Artifact slots (Fixed)
    }

    @Override
    protected int size(L2GameClient client) {
        return 53;
    }
}
