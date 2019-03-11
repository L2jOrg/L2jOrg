package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Gnacik
 */
public class ExNotifyPremiumItem extends IClientOutgoingPacket {
    public static final ExNotifyPremiumItem STATIC_PACKET = new ExNotifyPremiumItem();

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_NOTIFY_PREMIUM_ITEM.writeId(packet);
    }
}
