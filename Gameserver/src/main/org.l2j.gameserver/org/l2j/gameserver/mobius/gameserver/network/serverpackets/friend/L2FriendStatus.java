package org.l2j.gameserver.mobius.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * Support for "Chat with Friends" dialog. <br />
 * Inform player about friend online status change
 *
 * @author JIV
 */
public class L2FriendStatus extends IClientOutgoingPacket {
    public static final int MODE_OFFLINE = 0;
    public static final int MODE_ONLINE = 1;
    public static final int MODE_LEVEL = 2;
    public static final int MODE_CLASS = 3;

    private final int _type;
    private final int _objectId;
    private final int _classId;
    private final int _level;
    private final String _name;

    public L2FriendStatus(L2PcInstance player, int type) {
        _objectId = player.getObjectId();
        _classId = player.getActiveClass();
        _level = player.getLevel();
        _name = player.getName();
        _type = type;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.FRIEND_STATUS.writeId(packet);

        packet.putInt(_type);
        writeString(_name, packet);
        switch (_type) {
            case MODE_OFFLINE: {
                packet.putInt(_objectId);
                break;
            }
            case MODE_LEVEL: {
                packet.putInt(_level);
                break;
            }
            case MODE_CLASS: {
                packet.putInt(_classId);
                break;
            }
        }
    }
}
