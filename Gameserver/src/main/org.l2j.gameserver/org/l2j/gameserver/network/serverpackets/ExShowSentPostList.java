package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author Migi, DS
 */
public class ExShowSentPostList extends IClientOutgoingPacket {
    private final List<Message> _outbox;

    public ExShowSentPostList(int objectId) {
        _outbox = MailManager.getInstance().getOutbox(objectId);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_SENT_POST_LIST.writeId(packet);

        packet.putInt((int) (System.currentTimeMillis() / 1000));
        if ((_outbox != null) && (_outbox.size() > 0)) {
            packet.putInt(_outbox.size());
            for (Message msg : _outbox) {
                packet.putInt(msg.getId());
                writeString(msg.getSubject(), packet);
                writeString(msg.getReceiverName(), packet);
                packet.putInt(msg.isLocked() ? 0x01 : 0x00);
                packet.putInt(msg.getExpirationSeconds());
                packet.putInt(msg.isUnread() ? 0x01 : 0x00);
                packet.putInt(0x01);
                packet.putInt(msg.hasAttachments() ? 0x01 : 0x00);
                packet.putInt(0x00);
            }
        } else {
            packet.putInt(0x00);
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 9 + (isNullOrEmpty(_outbox) ? 4 : _outbox.size() * 90);
    }
}
