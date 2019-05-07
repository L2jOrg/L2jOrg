package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.Calendar;

import static java.util.Objects.nonNull;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_FRIEND_DETAIL_INFO.writeId(packet);

        packet.putInt(_objectId);

        if (_friend == null) {
            writeString(_name, packet);
            packet.putInt(0);
            packet.putInt(0);
            packet.putShort((short) 0);
            packet.putShort((short) 0);
            packet.putInt(0);
            packet.putInt(0);
            writeString("", packet);
            packet.putInt(0);
            packet.putInt(0);
            writeString("", packet);
            packet.putInt(1);
            writeString("", packet); // memo
        } else {
            writeString(_friend.getName(), packet);
            packet.putInt(_friend.isOnlineInt());
            packet.putInt(_friend.getObjectId());
            packet.putShort((short) _friend.getLevel());
            packet.putShort((short) _friend.getClassId().getId());
            packet.putInt(_friend.getClanId());
            packet.putInt(_friend.getClanCrestId());
            writeString(_friend.getClan() != null ? _friend.getClan().getName() : "", packet);
            packet.putInt(_friend.getAllyId());
            packet.putInt(_friend.getAllyCrestId());
            writeString(_friend.getClan() != null ? _friend.getClan().getAllyName() : "", packet);
            final Calendar createDate = _friend.getCreateDate();
            packet.put((byte) (createDate.get(Calendar.MONTH) + 1));
            packet.put((byte) createDate.get(Calendar.DAY_OF_MONTH));
            packet.putInt(_lastAccess);
            writeString("", packet); // memo
        }
    }

    @Override
    protected int size(L2GameClient client) {
        var size = 52;
        if(nonNull(_friend)) {
            size += _friend.getName().length() *2;
            var clan = _friend.getClan();
            if(nonNull(clan)) {
                size += clan.getName().length() * 2;
                size += clan.getAllyName().length() * 2;
            } else {
                size += 8;
            }
        } else {
            size += _name.length() * 2;
        }
        return size;
    }
}
