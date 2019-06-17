package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExUnReadMailCount extends ServerPacket {
    private final int _mailUnreadCount;

    public ExUnReadMailCount(L2PcInstance player) {
        _mailUnreadCount = (int) MailManager.getInstance().getUnreadCount(player);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_UN_READ_MAIL_COUNT);

        writeInt(_mailUnreadCount);
    }

}
