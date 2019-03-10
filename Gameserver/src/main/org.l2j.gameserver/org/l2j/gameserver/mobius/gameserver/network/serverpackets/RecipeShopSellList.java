package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2ManufactureItem;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class RecipeShopSellList extends IClientOutgoingPacket {
    private final L2PcInstance _buyer;
    private final L2PcInstance _manufacturer;

    public RecipeShopSellList(L2PcInstance buyer, L2PcInstance manufacturer) {
        _buyer = buyer;
        _manufacturer = manufacturer;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.RECIPE_SHOP_SELL_LIST.writeId(packet);

        packet.putInt(_manufacturer.getObjectId());
        packet.putInt((int) _manufacturer.getCurrentMp()); // Creator's MP
        packet.putInt(_manufacturer.getMaxMp()); // Creator's MP
        packet.putLong(_buyer.getAdena()); // Buyer Adena
        if (!_manufacturer.hasManufactureShop()) {
            packet.putInt(0x00);
        } else {
            packet.putInt(_manufacturer.getManufactureItems().size());
            for (L2ManufactureItem temp : _manufacturer.getManufactureItems().values()) {
                packet.putInt(temp.getRecipeId());
                packet.putInt(0x00); // unknown
                packet.putLong(temp.getCost());
            }
        }
    }
}
