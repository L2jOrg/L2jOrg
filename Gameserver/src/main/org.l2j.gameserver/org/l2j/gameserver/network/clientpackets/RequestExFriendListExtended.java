package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
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

        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new FriendList(activeChar));
    }
}
