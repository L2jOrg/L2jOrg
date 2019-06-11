package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * * @author Gnacik
 */
public class ShopPreviewInfo extends IClientOutgoingPacket {
    private final Map<Integer, Integer> _itemlist;

    public ShopPreviewInfo(Map<Integer, Integer> itemlist) {
        _itemlist = itemlist;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.SHOP_PREVIEW_INFO);

        writeInt(Inventory.PAPERDOLL_TOTALSLOTS);
        // Slots
        writeInt(getFromList(Inventory.PAPERDOLL_UNDER));
        writeInt(getFromList(Inventory.PAPERDOLL_REAR));
        writeInt(getFromList(Inventory.PAPERDOLL_LEAR));
        writeInt(getFromList(Inventory.PAPERDOLL_NECK));
        writeInt(getFromList(Inventory.PAPERDOLL_RFINGER));
        writeInt(getFromList(Inventory.PAPERDOLL_LFINGER));
        writeInt(getFromList(Inventory.PAPERDOLL_HEAD));
        writeInt(getFromList(Inventory.PAPERDOLL_RHAND));
        writeInt(getFromList(Inventory.PAPERDOLL_LHAND));
        writeInt(getFromList(Inventory.PAPERDOLL_GLOVES));
        writeInt(getFromList(Inventory.PAPERDOLL_CHEST));
        writeInt(getFromList(Inventory.PAPERDOLL_LEGS));
        writeInt(getFromList(Inventory.PAPERDOLL_FEET));
        writeInt(getFromList(Inventory.PAPERDOLL_CLOAK));
        writeInt(getFromList(Inventory.PAPERDOLL_RHAND));
        writeInt(getFromList(Inventory.PAPERDOLL_HAIR));
        writeInt(getFromList(Inventory.PAPERDOLL_HAIR2));
        writeInt(getFromList(Inventory.PAPERDOLL_RBRACELET));
        writeInt(getFromList(Inventory.PAPERDOLL_LBRACELET));
    }


    private int getFromList(int key) {
        return (_itemlist.getOrDefault(key, 0));
    }
}