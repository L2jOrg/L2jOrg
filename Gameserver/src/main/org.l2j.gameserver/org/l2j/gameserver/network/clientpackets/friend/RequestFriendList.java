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

import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

public final class RequestFriendList extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (isNull(player)) {
            return;
        }

        player.sendPacket(SystemMessageId.FRIENDS_LIST);
        player.sendPacket(player.getFriendList().stream().mapToObj(this::statusMessage).toArray(SystemMessage[]::new));
        player.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
    }

    private SystemMessage statusMessage(int friendId) {
        var friend = World.getInstance().findPlayer(friendId);
        SystemMessage sm;
        if(nonNull(friend)) {
            sm = getSystemMessage(SystemMessageId.S1_CURRENTLY_ONLINE).addString(friend.getName());
        } else {
            sm = getSystemMessage(SystemMessageId.S1_CURRENTLY_OFFLINE).addString(PlayerNameTable.getInstance().getNameById(friendId));
        }
        return sm;
    }
}
