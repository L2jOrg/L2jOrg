package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Support for "Chat with Friends" dialog. <br />
 * This packet is sent only at login.
 *
 * @author mrTJO, UnAfraid
 */
public class FriendList extends IClientOutgoingPacket {
    private final List<FriendInfo> _info = new LinkedList<>();

    public FriendList(L2PcInstance player) {
        for (int objId : player.getFriendList()) {
            final String name = CharNameTable.getInstance().getNameById(objId);
            final L2PcInstance player1 = L2World.getInstance().getPlayer(objId);

            boolean online = false;
            int classid = 0;
            int level = 0;

            if (player1 == null) {
                try (Connection con = DatabaseFactory.getInstance().getConnection();
                     PreparedStatement statement = con.prepareStatement("SELECT char_name, online, classid, level FROM characters WHERE charId = ?")) {
                    statement.setInt(1, objId);
                    try (ResultSet rset = statement.executeQuery()) {
                        if (rset.next()) {
                            _info.add(new FriendInfo(objId, rset.getString(1), rset.getInt(2) == 1, rset.getInt(3), rset.getInt(4)));
                        }
                    }
                } catch (Exception e) {
                    // Who cares?
                }
                continue;
            }

            if (player1.isOnline()) {
                online = true;
            }

            classid = player1.getClassId().getId();
            level = player1.getLevel();

            _info.add(new FriendInfo(objId, name, online, classid, level));
        }
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.FRIEND_LIST);

        writeInt(_info.size());
        for (FriendInfo info : _info) {
            writeInt(info._objId); // character id
            writeString(info._name);
            writeInt(info._online ? 0x01 : 0x00); // online
            writeInt(info._online ? info._objId : 0x00); // object id if online
            writeInt(info._classid);
            writeInt(info._level);
        }
    }

    private static class FriendInfo {
        int _objId;
        String _name;
        boolean _online;
        int _classid;
        int _level;

        public FriendInfo(int objId, String name, boolean online, int classid, int level) {
            _objId = objId;
            _name = name;
            _online = online;
            _classid = classid;
            _level = level;
        }
    }
}
