package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Support for "Chat with Friends" dialog. <br />
 * This packet is sent only at login.
 *
 * @author Tempy
 */
public class L2FriendList extends IClientOutgoingPacket {
    private final List<FriendInfo> _info = new LinkedList<>();

    public L2FriendList(L2PcInstance player) {
        for (int objId : player.getFriendList()) {
            final String name = CharNameTable.getInstance().getNameById(objId);
            final L2PcInstance player1 = L2World.getInstance().getPlayer(objId);
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.L2_FRIEND_LIST.writeId(packet);

        packet.putInt(_info.size());
        for (FriendInfo info : _info) {
            packet.putInt(info._objId); // character id
            writeString(info._name, packet);
            packet.putInt(info._online ? 0x01 : 0x00); // online
            packet.putInt(info._online ? info._objId : 0x00); // object id if online
            packet.putInt(info._level);
            packet.putInt(info._classId);
            packet.putShort((short) 0x00);
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 9 + _info.size() * 24 +  _info.stream().mapToInt(info -> info._name.length() * 2).sum();
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