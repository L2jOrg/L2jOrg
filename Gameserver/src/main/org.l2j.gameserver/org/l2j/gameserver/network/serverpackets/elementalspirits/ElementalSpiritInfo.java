package org.l2j.gameserver.network.serverpackets.elementalspirits;


import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ElementalSpiritInfo extends ServerPacket {

    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_ELEMENTAL_SPIRIT_INFO);
        writeByte(0x00); // show spirit info 1
        writeByte(2); // active Spirit element
        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);

        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);

        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);

        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);

        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);

        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);

        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);
        writeShort(1);

        writeShort(1);
        writeShort(2);
        writeShort(3);
        writeShort(1);
        writeShort(2);

        writeShort(1);
        writeShort(2);
        writeShort(3);
        writeShort(1);
        writeShort(2);

    }
}