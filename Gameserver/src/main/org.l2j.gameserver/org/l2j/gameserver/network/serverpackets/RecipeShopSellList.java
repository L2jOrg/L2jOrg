package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ManufactureItem;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class RecipeShopSellList extends IClientOutgoingPacket {
    private final L2PcInstance _buyer;
    private final L2PcInstance _manufacturer;

    public RecipeShopSellList(L2PcInstance buyer, L2PcInstance manufacturer) {
        _buyer = buyer;
        _manufacturer = manufacturer;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.RECIPE_SHOP_SELL_LIST);

        writeInt(_manufacturer.getObjectId());
        writeInt((int) _manufacturer.getCurrentMp()); // Creator's MP
        writeInt(_manufacturer.getMaxMp()); // Creator's MP
        writeLong(_buyer.getAdena()); // Buyer Adena
        if (!_manufacturer.hasManufactureShop()) {
            writeInt(0x00);
        } else {
            writeInt(_manufacturer.getManufactureItems().size());
            for (L2ManufactureItem temp : _manufacturer.getManufactureItems().values()) {
                writeInt(temp.getRecipeId());
                writeInt(0x00); // unknown
                writeLong(temp.getCost());
            }
        }
    }

}
