package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.LinkedList;
import java.util.List;

/**
 * Support for "Chat with Friends" dialog. <br />
 * This packet is sent only at login.
 *
 * @author Tempy
 */
public class L2FriendList extends ServerPacket {
    private final List<FriendInfo> _info = new LinkedList<>();

    public L2FriendList(Player player) {
        for (int objId : player.getFriendList()) {
            final String name = CharNameTable.getInstance().getNameById(objId);
            final Player player1 = L2World.getInstance().getPlayer(objId);
            boolean online = false;
            int level = 0;
            int classId = 0;

            if (player1 != null) {
                online = true;
                level = player1.getLevel();
                classId = player1.getClassId().getId();
            } else {
                level = CharNameTable.getInstance().getLevelById(objId);
                classId = CharNameTable.getInstance().getClassIdById(objId);
            }
            _info.add(new FriendInfo(objId, name, online, level, classId));
        }
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.L2_FRIEND_LIST);

        writeInt(_info.size());
        for (FriendInfo info : _info) {
            writeInt(info._objId); // character id
            writeString(info._name);
            writeInt(info._online ? 0x01 : 0x00); // online
            writeInt(info._online ? info._objId : 0x00); // object id if online
            writeInt(info._level);
            writeInt(info._classId);
            writeShort((short) 0x00);
        }
    }


    private static class FriendInfo {
        int _objId;
        String _name;
        int _level;
        int _classId;
        boolean _online;

        public FriendInfo(int objId, String name, boolean online, int level, int classId) {
            _objId = objId;
            _name = name;
            _online = online;
            _level = level;
            _classId = classId;
        }
    }
}