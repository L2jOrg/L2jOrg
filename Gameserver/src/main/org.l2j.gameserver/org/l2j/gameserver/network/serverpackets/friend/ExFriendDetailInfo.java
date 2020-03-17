package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Sdw
 */
public class ExFriendDetailInfo extends ServerPacket {
    private final int _objectId;
    private Player friend;
    private FriendInfo info;
    private final String _name;

    public ExFriendDetailInfo(Player player, String name) {
        _objectId = player.getObjectId();
        _name = name;

        var friendId = PlayerNameTable.getInstance().getIdByName(name);

        friend = World.getInstance().findPlayer(friendId);
        if(isNull(friend)) {
            info = new FriendInfo(friendId, getDAO(PlayerDAO.class).findFriendData(friendId));
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_FRIEND_DETAIL_INFO);

        writeInt(_objectId);

        if (isNull(friend)) {
            WriteFriendInfo();
        } else {
            writePlayerInfo();
        }
    }

    private void writePlayerInfo() {
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

        var createDate = friend.getCreateDate();
        writeByte(createDate.getMonthValue());
        writeByte(createDate.getDayOfMonth());

        writeInt(-1);
        writeString(""); //TODO  memo
    }

    private void WriteFriendInfo() {
        writeString(_name);
        writeInt(info.online);
        writeInt(info.online ? info.objectId : 0);
        writeShort(info.level);
        writeShort(info.classId);
        writeInt(info.clanId);

        Clan clan;
        if(info.clanId > 0 && nonNull(clan = ClanTable.getInstance().getClan(info.clanId))) {
            writeInt(clan.getCrestId());
            writeString(clan.getName());
            writeInt(clan.getAllyId());
            writeInt(clan.getAllyCrestId());
            writeString(clan.getAllyName());
        } else {
            writeInt(0);
            writeString("");
            writeInt(0);
            writeInt(0);
            writeString("");
        }

        if(nonNull(info.createDate)) {
            writeByte(info.createDate.getMonthValue());
            writeByte(info.createDate.getDayOfMonth());
        } else  {
            writeByte(0);
            writeByte(0);
        }
        writeInt((int) ((System.currentTimeMillis() - info.lastAccess) / 1000));
        writeString(""); //TODO  memo
    }
}
