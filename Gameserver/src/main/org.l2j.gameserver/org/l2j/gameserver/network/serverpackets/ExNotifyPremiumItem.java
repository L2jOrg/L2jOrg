package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Gnacik
 */
public class ExNotifyPremiumItem extends ServerPacket {
    public static final ExNotifyPremiumItem STATIC_PACKET = new ExNotifyPremiumItem();

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_NOTIFY_PREMIUM_ITEM);
    }

}
