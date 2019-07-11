package org.l2j.gameserver.network.serverpackets;


import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExTutorialShowId extends ServerPacket {
    private final int _id;

    public ExTutorialShowId(int id) {
        this._id = id;
    }

    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_TUTORIAL_SHOW_ID);
        writeInt(this._id);
    }
}
