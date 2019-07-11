package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author Migi, DS
 */
public class ExShowSentPostList extends ServerPacket {
    private final List<Message> _outbox;

    public ExShowSentPostList(int objectId) {
        _outbox = MailManager.getInstance().getOutbox(objectId);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_SENT_POST_LIST);

        writeInt((int) (System.currentTimeMillis() / 1000));
        if ((_outbox != null) && (_outbox.size() > 0)) {
            writeInt(_outbox.size());
            for (Message msg : _outbox) {
                writeInt(msg.getId());
                writeString(msg.getSubject());
                writeString(msg.getReceiverName());
                writeInt(msg.isLocked() ? 0x01 : 0x00);
                writeInt(msg.getExpirationSeconds());
                writeInt(msg.isUnread() ? 0x01 : 0x00);
                writeInt(0x01);
                writeInt(msg.hasAttachments() ? 0x01 : 0x00);
                writeInt(0x00);
            }
        } else {
            writeInt(0x00);
        }
    }

}
