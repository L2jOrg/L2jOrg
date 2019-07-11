package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExShowSentPostList;

/**
 * @author Migi, DS
 */
public final class RequestSentPostList extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if ((activeChar == null) || !Config.ALLOW_MAIL) {
            return;
        }

        // if (!activeChar.isInsideZone(ZoneId.PEACE))
        // {
        // activeChar.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
        // return;
        // }

        client.sendPacket(new ExShowSentPostList(activeChar.getObjectId()));
    }
}
