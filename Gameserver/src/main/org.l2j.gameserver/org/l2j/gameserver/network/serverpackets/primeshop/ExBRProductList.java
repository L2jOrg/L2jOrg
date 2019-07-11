package org.l2j.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

/**
 * @author UnAfraid
 */
public class ExBRProductList extends ServerPacket {
    private final Player _activeChar;
    private final int _type;
    private final Collection<PrimeShopProduct> _primeList;

    public ExBRProductList(Player activeChar, int type, Collection<PrimeShopProduct> items) {
        _activeChar = activeChar;
        _type = type;
        _primeList = items;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_BR_PRODUCT_LIST);

        writeLong(_activeChar.getAdena()); // Adena
        writeLong(0x00); // Hero coins
        writeByte((byte) _type); // Type 0 - Home, 1 - History, 2 - Favorites
        writeInt(_primeList.size());
        for (PrimeShopProduct brItem : _primeList) {
            writeInt(brItem.getId());
            writeByte(brItem.getCategory());
            writeByte(brItem.getPaymentType()); // Payment Type: 0 - Prime Points, 1 - Adena, 2 - Hero Coins
            writeInt(brItem.getPrice());
            writeByte(brItem.getPanelType()); // Item Panel Type: 0 - None, 1 - Event, 2 - Sale, 3 - New, 4 - Best
            writeInt(brItem.getRecommended()); // Recommended: (bit flags) 1 - Top, 2 - Left, 4 - Right
            writeInt(brItem.getStartSale());
            writeInt(brItem.getEndSale());
            writeByte(brItem.getDaysOfWeek());
            writeByte(brItem.getStartHour());
            writeByte(brItem.getStartMinute());
            writeByte(brItem.getStopHour());
            writeByte(brItem.getStopMinute());
            writeInt(brItem.getStock());
            writeInt(brItem.getTotal());
            writeByte(brItem.getSalePercent());
            writeByte(brItem.getMinLevel());
            writeByte(brItem.getMaxLevel());
            writeInt(brItem.getMinBirthday());
            writeInt(brItem.getMaxBirthday());
            writeInt(brItem.getRestrictionDay());
            writeInt(brItem.getAvailableCount());
            writeByte((byte) brItem.getItems().size());
            for (PrimeShopItem item : brItem.getItems()) {
                writeInt(item.getId());
                writeInt((int) item.getCount());
                writeInt(item.getWeight());
                writeInt(item.isTradable());
            }
        }
    }

}