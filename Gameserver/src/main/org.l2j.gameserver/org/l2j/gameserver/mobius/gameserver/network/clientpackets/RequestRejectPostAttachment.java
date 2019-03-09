package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.enums.MailType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.entity.Message;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.nio.ByteBuffer;

/**
 * @author Migi, DS
 */
public final class RequestRejectPostAttachment extends IClientIncomingPacket
{
    private int _msgId;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _msgId = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        if (!Config.ALLOW_MAIL || !Config.ALLOW_ATTACHMENTS)
        {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("rejectattach"))
        {
            return;
        }

        if (!activeChar.isInsideZone(ZoneId.PEACE))
        {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }

        final Message msg = MailManager.getInstance().getMessage(_msgId);
        if (msg == null)
        {
            return;
        }

        if (msg.getReceiverId() != activeChar.getObjectId())
        {
            Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to reject not own attachment!", Config.DEFAULT_PUNISH);
            return;
        }

        if (!msg.hasAttachments() || (msg.getMailType() != MailType.REGULAR))
        {
            return;
        }

        MailManager.getInstance().sendMessage(new Message(msg));

        client.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_RETURNED);
        client.sendPacket(new ExChangePostState(true, _msgId, Message.REJECTED));

        final L2PcInstance sender = L2World.getInstance().getPlayer(msg.getSenderId());
        if (sender != null)
        {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_RETURNED_THE_MAIL);
            sm.addString(activeChar.getName());
            sender.sendPacket(sm);
        }
    }
}
