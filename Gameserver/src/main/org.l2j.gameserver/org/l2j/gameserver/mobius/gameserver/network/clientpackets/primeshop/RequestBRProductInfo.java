package org.l2j.gameserver.mobius.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRProductInfo extends IClientIncomingPacket {
    private int _brId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _brId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player != null) {
            PrimeShopData.getInstance().showProductInfo(player, _brId);
        }
    }
}
