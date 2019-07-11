package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.ManufactureItem;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class RecipeShopSellList extends ServerPacket {
    private final Player _buyer;
    private final Player _manufacturer;

    public RecipeShopSellList(Player buyer, Player manufacturer) {
        _buyer = buyer;
        _manufacturer = manufacturer;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.RECIPE_SHOP_SELL_LIST);

        writeInt(_manufacturer.getObjectId());
        writeInt((int) _manufacturer.getCurrentMp()); // Creator's MP
        writeInt(_manufacturer.getMaxMp()); // Creator's MP
        writeLong(_buyer.getAdena()); // Buyer Adena
        if (!_manufacturer.hasManufactureShop()) {
            writeInt(0x00);
        } else {
            writeInt(_manufacturer.getManufactureItems().size());
            for (ManufactureItem temp : _manufacturer.getManufactureItems().values()) {
                writeInt(temp.getRecipeId());
                writeInt(0x00); // unknown
                writeLong(temp.getCost());
            }
        }
    }

}
