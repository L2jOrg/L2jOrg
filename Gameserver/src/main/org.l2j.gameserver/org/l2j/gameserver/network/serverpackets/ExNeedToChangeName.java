package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * Dialog with input field<br>
 * type 0 = char name (Selection screen)<br>
 * type 1 = clan name
 *
 * @author JIV
 */
public class ExNeedToChangeName extends IClientOutgoingPacket {
    private final int _type;
    private final int _subType;
    private final String _name;

    public ExNeedToChangeName(int type, int subType, String name) {
        super();
        _type = type;
        _subType = subType;
        _name = name;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_NEED_TO_CHANGE_NAME.writeId(packet);

        packet.putInt(_type);
        packet.putInt(_subType);
        writeString(_name, packet);
    }
}
