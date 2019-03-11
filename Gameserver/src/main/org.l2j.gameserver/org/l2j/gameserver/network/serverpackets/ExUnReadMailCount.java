package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExUnReadMailCount extends IClientOutgoingPacket {
    private final int _mailUnreadCount;

    public ExUnReadMailCount(L2PcInstance player) {
        _mailUnreadCount = (int) MailManager.getInstance().getUnreadCount(player);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_UN_READ_MAIL_COUNT.writeId(packet);

        packet.putInt(_mailUnreadCount);
    }
}
