/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.friend.FriendRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestFriendDel extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestFriendDel.class);

    private String _name;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        SystemMessage sm;

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final int id = CharNameTable.getInstance().getIdByName(_name);

        if (id == -1) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_NOT_ON_YOUR_FRIEND_LIST);
            sm.addString(_name);
            activeChar.sendPacket(sm);
            return;
        }

        if (!activeChar.getFriendList().contains(id)) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_NOT_ON_YOUR_FRIEND_LIST);
            sm.addString(_name);
            activeChar.sendPacket(sm);
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM character_friends WHERE (charId=? AND friendId=?) OR (charId=? AND friendId=?)")) {
            statement.setInt(1, activeChar.getObjectId());
            statement.setInt(2, id);
            statement.setInt(3, id);
            statement.setInt(4, activeChar.getObjectId());
            statement.execute();

            // Player deleted from your friend list
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIENDS_LIST_2);
            sm.addString(_name);
            activeChar.sendPacket(sm);

            activeChar.getFriendList().remove(Integer.valueOf(id));
            activeChar.sendPacket(new FriendRemove(_name, 1));

            final L2PcInstance player = L2World.getInstance().getPlayer(_name);
            if (player != null) {
                player.getFriendList().remove(Integer.valueOf(activeChar.getObjectId()));
                player.sendPacket(new FriendRemove(activeChar.getName(), 1));
            }
        } catch (Exception e) {
            LOGGER.warn("could not del friend objectid: ", e);
        }
    }
}
