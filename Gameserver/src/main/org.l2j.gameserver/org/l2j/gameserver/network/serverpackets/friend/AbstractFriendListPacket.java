package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

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
