package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.entity.Message;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExReplyReceivedPost;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.nio.ByteBuffer;

/**
 * @author Migi, DS
 */
public final class RequestReceivedPost extends IClientIncomingPacket {
    private int _msgId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _msgId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || !Config.ALLOW_MAIL) {
            return;
        }

        final Message msg = MailManager.getInstance().getMessage(_msgId);
        if (msg == null) {
            return;
        }

        if (!activeChar.isInsideZone(ZoneId.PEACE) && msg.hasAttachments()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }

        if (msg.getReceiverId() != activeChar.getObjectId()) {
            Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to receive not own post!", Config.DEFAULT_PUNISH);
            return;
        }

        if (msg.isDeletedByReceiver()) {
            return;
        }

        client.sendPacket(new ExReplyReceivedPost(msg));
        client.sendPacket(new ExChangePostState(true, _msgId, Message.READED));
        msg.markAsRead();
    }
}
