package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExDieInfo extends ServerPacket {


    public ExDieInfo() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_DIE_INFO);

        writeShort(0);
        writeShort(0); // Nb damages
        writeShort(1); // Activate damage list button ?

        for (int i = 0 ; i < 10; i++) { // Loop over all damages received by player
            writeInt(0); // Attacker id
            writeShort(0); // unknown
            writeShort(0); // unknown
            writeShort(0); // unknown
            writeDouble(0); // Damages / 10 ?? wtf
            writeShort(1); // unknown
            writeShort(1); // unknown
        }

        writeShort(1); // unknown but can take several values , during test got 1 and 4
    }

}