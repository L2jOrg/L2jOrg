package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.util.Calendar;

/**
 * @author Sdw
 */
public class ExFriendDetailInfo extends IClientOutgoingPacket {
    private final int _objectId;
    private final L2PcInstance _friend;
    private final String _name;
    private final int _lastAccess;

    public ExFriendDetailInfo(L2PcInstance player, String name) {
        _objectId = player.getObjectId();
        _name = name;
        _friend = L2World.getInstance().getPlayer(_name);
        _lastAccess = _friend.isBlocked(player) ? 0 : _friend.isOnline() ? (int) System.currentTimeMillis() : (int) (System.currentTimeMillis() - _friend.getLastAccess()) / 1000;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_FRIEND_DETAIL_INFO);

        writeInt(_objectId);

        if (_friend == null) {
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
            writeString(_friend.getName());
            writeInt(_friend.isOnlineInt());
            writeInt(_friend.getObjectId());
            writeShort((short) _friend.getLevel());
            writeShort((short) _friend.getClassId().getId());
            writeInt(_friend.getClanId());
            writeInt(_friend.getClanCrestId());
            writeString(_friend.getClan() != null ? _friend.getClan().getName() : "");
            writeInt(_friend.getAllyId());
            writeInt(_friend.getAllyCrestId());
            writeString(_friend.getClan() != null ? _friend.getClan().getAllyName() : "");
            final Calendar createDate = _friend.getCreateDate();
            writeByte((byte) (createDate.get(Calendar.MONTH) + 1));
            writeByte((byte) createDate.get(Calendar.DAY_OF_MONTH));
            writeInt(_lastAccess);
            writeString(""); // memo
        }
    }
}
