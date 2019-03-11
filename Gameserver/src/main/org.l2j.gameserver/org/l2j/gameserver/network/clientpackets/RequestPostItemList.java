package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExReplyPostItemList;

import java.nio.ByteBuffer;

/**
 * @author Migi, DS
 */
public final class RequestPostItemList extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        if (!Config.ALLOW_MAIL || !Config.ALLOW_ATTACHMENTS) {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!activeChar.isInsideZone(ZoneId.PEACE)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }

        client.sendPacket(new ExReplyPostItemList(1, activeChar));
        client.sendPacket(new ExReplyPostItemList(2, activeChar));
    }
}
