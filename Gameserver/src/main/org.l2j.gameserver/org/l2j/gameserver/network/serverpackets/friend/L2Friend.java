package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * Support for "Chat with Friends" dialog. <br />
 * Add new friend or delete.
 *
 * @author JIV
 */
public class L2Friend extends IClientOutgoingPacket {
    private final boolean _action;
    private final boolean _online;
    private final int _objid;
    private final String _name;

    /**
     * @param action - true for adding, false for remove
     * @param objId
     */
    public L2Friend(boolean action, int objId) {
        _action = action;
        _objid = objId;
        _name = CharNameTable.getInstance().getNameById(objId);
        _online = L2World.getInstance().getPlayer(objId) != null;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.L2_FRIEND.writeId(packet);

        packet.putInt(_action ? 1 : 3); // 1-add 3-remove
        packet.putInt(_objid);
        writeString(_name, packet);
        packet.putInt(_online ? 1 : 0);
        packet.putInt(_online ? _objid : 0);
    }
}
