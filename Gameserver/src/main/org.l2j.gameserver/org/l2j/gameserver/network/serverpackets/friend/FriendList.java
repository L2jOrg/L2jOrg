package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * Support for "Chat with Friends" dialog. <br />
 * This packet is sent only at login.
 *
 * @author mrTJO, UnAfraid
 */
public class FriendList extends AbstractFriendListPacket {

    public FriendList(Player player) {
        super(player);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.FRIEND_LIST);

        writeInt(info.size());
        for (FriendInfo info : info) {
            writeInt(info.objectId);
            writeString(info.name);
            writeInt(info.online);
            writeInt(info.online ? info.objectId : 0x00); // object id if online
            writeInt(info.classId);
            writeInt(info.level);
        }
    }
}
