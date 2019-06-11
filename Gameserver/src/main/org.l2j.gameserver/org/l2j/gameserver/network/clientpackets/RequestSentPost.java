package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExReplySentPost;
import org.l2j.gameserver.util.Util;

import java.nio.ByteBuffer;

/**
 * @author Migi, DS
 */
public final class RequestSentPost extends IClientIncomingPacket {
    private int _msgId;

    @Override
    public void readImpl() {
        _msgId = readInt();
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

        if (msg.getSenderId() != activeChar.getObjectId()) {
            Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to read not own post!", Config.DEFAULT_PUNISH);
            return;
        }

        if (msg.isDeletedBySender()) {
            return;
        }

        client.sendPacket(new ExReplySentPost(msg));
    }
}
