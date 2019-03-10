package org.l2j.gameserver.mobius.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.primeshop.PrimeShopGroup;
import org.l2j.gameserver.mobius.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Gnacik
 */
public class ExBRProductInfo extends IClientOutgoingPacket {
    private final PrimeShopGroup _item;
    private final int _charPoints;
    private final long _charAdena;

    public ExBRProductInfo(PrimeShopGroup item, L2PcInstance player) {
        _item = item;
        _charPoints = player.getPrimePoints();
        _charAdena = player.getAdena();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BR_PRODUCT_INFO.writeId(packet);

        packet.putInt(_item.getBrId());
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
