package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExUnReadMailCount extends ServerPacket {
    private final int _mailUnreadCount;

    public ExUnReadMailCount(Player player) {
        _mailUnreadCount = (int) MailManager.getInstance().getUnreadCount(player);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_UN_READ_MAIL_COUNT);

        writeInt(_mailUnreadCount);
    }

}
