package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestFriendList extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        SystemMessage sm;

        // ======<Friend List>======
        activeChar.sendPacket(SystemMessageId.FRIENDS_LIST);

        Player friend;
        for (int id : activeChar.getFriendList()) {
            // int friendId = rset.getInt("friendId");
            final String friendName = CharNameTable.getInstance().getNameById(id);

            if (friendName == null) {
                continue;
            }

            friend = L2World.getInstance().getPlayer(friendName);

            if ((friend == null) || !friend.isOnline()) {
                // (Currently: Offline)
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CURRENTLY_OFFLINE);
                sm.addString(friendName);
            } else {
                // (Currently: Online)
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CURRENTLY_ONLINE);
                sm.addString(friendName);
            }

            activeChar.sendPacket(sm);
        }

        // =========================
        activeChar.sendPacket(SystemMessageId.EMPTY_3);
    }
}
