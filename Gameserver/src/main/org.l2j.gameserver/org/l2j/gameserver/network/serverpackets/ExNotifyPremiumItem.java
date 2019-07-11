package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Gnacik
 */
public class ExNotifyPremiumItem extends ServerPacket {
    public static final ExNotifyPremiumItem STATIC_PACKET = new ExNotifyPremiumItem();

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_NOTIFY_PREMIUM_ITEM);
    }

}
