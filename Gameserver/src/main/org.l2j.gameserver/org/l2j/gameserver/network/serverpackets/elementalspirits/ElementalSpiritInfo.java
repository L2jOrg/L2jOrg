package org.l2j.gameserver.network.serverpackets.elementalspirits;


import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

public class ElementalSpiritInfo extends IClientOutgoingPacket {

    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_ELEMENTAL_SPIRIT_INFO);
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