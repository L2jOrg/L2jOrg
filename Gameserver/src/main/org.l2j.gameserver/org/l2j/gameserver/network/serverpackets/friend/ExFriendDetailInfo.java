package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Calendar;

/**
 * @author Sdw
 */
public class ExFriendDetailInfo extends ServerPacket {
    private final int _objectId;
    private final Player friend;
    private final String _name;
    private final int lastAccess;

    public ExFriendDetailInfo(Player player, String name) {
        _objectId = player.getObjectId();
        _name = name;
        friend = World.getInstance().findPlayer(_name);
        lastAccess = friend.isBlocked(player) ? 0 : friend.isOnline() ? 0 : (int) (System.currentTimeMillis() - friend.getLastAccess()) / 1000;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_FRIEND_DETAIL_INFO);

        writeInt(_objectId);

        if (friend == null) {
            writeString(_name);
            writeInt(0);
            writeInt(0);
            writeShort((short) 0);
            writeShort((short) 0);
            writeInt(0);
            writeInt(0);
            writeString("");
            writeInt(0);
            writeInt(0);
            writeString("");
            writeInt(1);
            writeString(""); // memo
        } else {
            writeString(friend.getName());
            writeInt(friend.isOnline());
            writeInt(friend.isOnline() ? friend.getObjectId() : 0);
            writeShort(friend.getLevel());
            writeShort(friend.getClassId().getId());
            writeInt(friend.getClanId());
            writeInt(friend.getClanCrestId());
            writeString(friend.getClan() != null ? friend.getClan().getName() : "");
            writeInt(friend.getAllyId());
            writeInt(friend.getAllyCrestId());
            writeString(friend.getClan() != null ? friend.getClan().getAllyName() : "");
            final Calendar createDate = friend.getCreateDate();
            writeByte((byte) (createDate.get(Calendar.MONTH) + 1));
            writeByte((byte) createDate.get(Calendar.DAY_OF_MONTH));
            writeInt(lastAccess);
            writeString(""); // memo
        }
    }
}
