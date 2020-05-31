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
        writeShort(9); // Nb damages


        for (int i = 0 ; i < 7; i++) { // Loop over all damages received by player
            writeShort(1); // Start damage ?
            writeInt(20120); // Attacker id
            writeShort(0); // FALL DAMAGES if 1 or + and bug all value
            writeInt(0); // Skill ID
            writeDouble(30); // Damages
            writeShort(5); // 0 = other damages 1 = normal attack 2 = fall damages 3 = water damages 4+ = other damages
        }

        for (int i = 0 ; i < 2; i++) { // Loop over all damages received by player
            writeShort(2); // Start damage ?
            writeString("LoropetikA"); // Attacker name
            writeShort(0); // unknown
            writeInt(1177); // Skill ID
            writeDouble(116); // Damages
            writeShort(4); // unknown
        }

    }

}