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
