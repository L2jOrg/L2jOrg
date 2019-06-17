package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.friend.FriendList;

/**
 * @author mrTJO & UnAfraid
 */
public final class RequestExFriendListExtended extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        if (!Config.ALLOW_MAIL) {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new FriendList(activeChar));
    }
}
