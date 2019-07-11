package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * Support for "Chat with Friends" dialog. <br />
 * Inform player about friend online status change
 *
 * @author JIV
 */
public class L2FriendStatus extends ServerPacket {
    public static final int MODE_OFFLINE = 0;
    public static final int MODE_ONLINE = 1;
    public static final int MODE_LEVEL = 2;
    public static final int MODE_CLASS = 3;

    private final int _type;
    private final int _objectId;
    private final int _classId;
    private final int _level;
    private final String _name;

    public L2FriendStatus(Player player, int type) {
        _objectId = player.getObjectId();
        _classId = player.getActiveClass();
        _level = player.getLevel();
        _name = player.getName();
        _type = type;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.FRIEND_STATUS);

        writeInt(_type);
        writeString(_name);
        switch (_type) {
            case MODE_OFFLINE -> writeInt(_objectId);
            case MODE_LEVEL -> writeInt(_level);
            case MODE_CLASS -> writeInt(_classId);
        }
    }

}
