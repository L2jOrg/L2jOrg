package org.l2j.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Gnacik
 */
public class ExBRProductInfo extends IClientOutgoingPacket {
    private final PrimeShopProduct _item;
    private final int _charPoints;
    private final long _charAdena;

    public ExBRProductInfo(PrimeShopProduct item, L2PcInstance player) {
        _item = item;
        _charPoints = player.getPrimePoints();
        _charAdena = player.getAdena();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BR_PRODUCT_INFO.writeId(packet);

        packet.putInt(_item.getId());
        packet.putInt(_item.getPrice());
        packet.putInt(_item.getItems().size());
        for (PrimeShopItem item : _item.getItems()) {
            packet.putInt(item.getId());
            packet.putInt((int) item.getCount());
            packet.putInt(item.getWeight());
            packet.putInt(item.isTradable());
        }
        packet.putLong(_charAdena);
        packet.putLong(_charPoints);
        packet.putLong(0x00); // Hero coins
    }
}
