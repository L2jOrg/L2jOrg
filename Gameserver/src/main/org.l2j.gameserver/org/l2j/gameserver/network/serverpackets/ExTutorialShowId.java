package org.l2j.gameserver.network.serverpackets;


import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExTutorialShowId extends ServerPacket {
    private final int _id;

    public ExTutorialShowId(int id) {
        this._id = id;
    }

    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_TUTORIAL_SHOW_ID);
        writeInt(this._id);
    }
}
