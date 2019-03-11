package org.l2j.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.primeshop.PrimeShopGroup;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author UnAfraid
 */
public class ExBRProductList extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final int _type;
    private final Collection<PrimeShopGroup> _primeList;

    public ExBRProductList(L2PcInstance activeChar, int type, Collection<PrimeShopGroup> items) {
        _activeChar = activeChar;
        _type = type;
        _primeList = items;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BR_PRODUCT_LIST.writeId(packet);

        packet.putLong(_activeChar.getAdena()); // Adena
        packet.putLong(0x00); // Hero coins
        packet.put((byte) _type); // Type 0 - Home, 1 - History, 2 - Favorites
        packet.putInt(_primeList.size());
        for (PrimeShopGroup brItem : _primeList) {
            packet.putInt(brItem.getBrId());
            packet.put((byte) brItem.getCat());
            packet.put((byte) brItem.getPaymentType()); // Payment Type: 0 - Prime Points, 1 - Adena, 2 - Hero Coins
            packet.putInt(brItem.getPrice());
            packet.put((byte) brItem.getPanelType()); // Item Panel Type: 0 - None, 1 - Event, 2 - Sale, 3 - New, 4 - Best
            packet.putInt(brItem.getRecommended()); // Recommended: (bit flags) 1 - Top, 2 - Left, 4 - Right
            packet.putInt(brItem.getStartSale());
            packet.putInt(brItem.getEndSale());
            packet.put((byte) brItem.getDaysOfWeek());
            packet.put((byte) brItem.getStartHour());
            packet.put((byte) brItem.getStartMinute());
            packet.put((byte) brItem.getStopHour());
            packet.put((byte) brItem.getStopMinute());
            packet.putInt(brItem.getStock());
            packet.putInt(brItem.getTotal());
            packet.put((byte) brItem.getSalePercent());
            packet.put((byte) brItem.getMinLevel());
            packet.put((byte) brItem.getMaxLevel());
            packet.putInt(brItem.getMinBirthday());
            packet.putInt(brItem.getMaxBirthday());
            packet.putInt(brItem.getRestrictionDay());
            packet.putInt(brItem.getAvailableCount());
            packet.put((byte) brItem.getItems().size());
            for (PrimeShopItem item : brItem.getItems()) {
                packet.putInt(item.getId());
                packet.putInt((int) item.getCount());
                packet.putInt(item.getWeight());
                packet.putInt(item.isTradable());
            }
        }
    }
}