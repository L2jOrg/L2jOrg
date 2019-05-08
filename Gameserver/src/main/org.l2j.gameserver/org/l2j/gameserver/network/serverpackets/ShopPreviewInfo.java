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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SHOP_PREVIEW_INFO.writeId(packet);

        packet.putInt(Inventory.PAPERDOLL_TOTALSLOTS);
        // Slots
        packet.putInt(getFromList(Inventory.PAPERDOLL_UNDER));
        packet.putInt(getFromList(Inventory.PAPERDOLL_REAR));
        packet.putInt(getFromList(Inventory.PAPERDOLL_LEAR));
        packet.putInt(getFromList(Inventory.PAPERDOLL_NECK));
        packet.putInt(getFromList(Inventory.PAPERDOLL_RFINGER));
        packet.putInt(getFromList(Inventory.PAPERDOLL_LFINGER));
        packet.putInt(getFromList(Inventory.PAPERDOLL_HEAD));
        packet.putInt(getFromList(Inventory.PAPERDOLL_RHAND));
        packet.putInt(getFromList(Inventory.PAPERDOLL_LHAND));
        packet.putInt(getFromList(Inventory.PAPERDOLL_GLOVES));
        packet.putInt(getFromList(Inventory.PAPERDOLL_CHEST));
        packet.putInt(getFromList(Inventory.PAPERDOLL_LEGS));
        packet.putInt(getFromList(Inventory.PAPERDOLL_FEET));
        packet.putInt(getFromList(Inventory.PAPERDOLL_CLOAK));
        packet.putInt(getFromList(Inventory.PAPERDOLL_RHAND));
        packet.putInt(getFromList(Inventory.PAPERDOLL_HAIR));
        packet.putInt(getFromList(Inventory.PAPERDOLL_HAIR2));
        packet.putInt(getFromList(Inventory.PAPERDOLL_RBRACELET));
        packet.putInt(getFromList(Inventory.PAPERDOLL_LBRACELET));
    }

    @Override
    protected int size(L2GameClient client) {
        return 85;
    }

    private int getFromList(int key) {
        return (_itemlist.getOrDefault(key, 0));
    }
}