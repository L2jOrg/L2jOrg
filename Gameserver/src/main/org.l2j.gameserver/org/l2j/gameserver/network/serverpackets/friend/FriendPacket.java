package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * Support for "Chat with Friends" dialog. <br />
 * Add new friend or delete.
 *
 * @author JIV
 */
public class FriendPacket extends ServerPacket {
    private final boolean _action;
    private final boolean _online;
    private final int _objid;
    private final String _name;

    /**
     * @param action - true for adding, false for remove
     * @param objId
     */
    public FriendPacket(boolean action, int objId) {
        _action = action;
        _objid = objId;
        _name = PlayerNameTable.getInstance().getNameById(objId);
        _online = World.getInstance().getPlayer(objId) != null;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.L2_FRIEND);

        writeInt(_action ? 1 : 3); // 1-add 3-remove
        writeInt(_objid);
        writeString(_name);
        writeInt(_online ? 1 : 0);
        writeInt(_online ? _objid : 0);
    }

}
