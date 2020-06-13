/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.friend.FriendRemove;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

public final class RequestFriendDel extends ClientPacket {

    private String name;

    @Override
    public void readImpl() {
        name = readString();
    }

    @Override
    public void runImpl() {
        SystemMessage sm;

        final Player activeChar = client.getPlayer();
        if (isNull(activeChar)) {
            return;
        }

        final int id = PlayerNameTable.getInstance().getIdByName(name);

        if (id == -1) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_NOT_ON_YOUR_FRIEND_LIST);
            sm.addString(name);
            activeChar.sendPacket(sm);
            return;
        }

        if (!activeChar.getFriendList().contains(id)) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_NOT_ON_YOUR_FRIEND_LIST);
            sm.addString(name);
            activeChar.sendPacket(sm);
            return;
        }

        getDAO(PlayerDAO.class).deleteFriendship(activeChar.getObjectId(), id);

        sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIENDS_LIST);
        sm.addString(name);
        activeChar.sendPacket(sm);

        activeChar.getFriendList().remove(id);
        activeChar.sendPacket(new FriendRemove(name, 1));

        final Player player = World.getInstance().findPlayer(name);
        if (player != null) {
            player.getFriendList().remove(activeChar.getObjectId());
            player.sendPacket(new FriendRemove(activeChar.getName(), 1));
        }
    }
}
