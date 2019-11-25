package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.EnumMap;

/**
 * @author Gnacik
 * @author JoeAlisson
 */
public class ShopPreviewInfo extends ServerPacket {
    private final EnumMap<InventorySlot, Integer> items;

    public ShopPreviewInfo(EnumMap<InventorySlot, Integer> items) {
        this.items = items;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHOP_PREVIEW_INFO);

        writeInt(InventorySlot.TOTAL_SLOTS);

        var paperdool = getPaperdollOrder();
        for (int i = 0; i < 19; i++) {
            writeInt(getFromList(paperdool[i]));
        }
    }


    private int getFromList(InventorySlot key) {
        return items.getOrDefault(key, 0);
    }
}