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
package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

abstract class AbstractFriendListPacket extends ServerPacket {
    protected final List<FriendInfo> info;

    AbstractFriendListPacket(Player player) {
        info = player.getFriendList().stream().mapToObj(this::friendInfo).collect(Collectors.toList());
    }

    private FriendInfo friendInfo(int friendId) {
        var friend = World.getInstance().findPlayer(friendId);
        if(nonNull(friend)) {
            return new FriendInfo(friendId, friend.getName(), true, friend.getLevel(), friend.getClassId().getId());
        }
        return new FriendInfo(friendId, getDAO(PlayerDAO.class).findFriendData(friendId));
    }
}
