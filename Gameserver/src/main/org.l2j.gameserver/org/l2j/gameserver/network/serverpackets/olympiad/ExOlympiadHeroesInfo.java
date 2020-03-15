package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExOlympiadHeroesInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_OLYMPIAD_HERO_AND_LEGEND_INFO);

        writeShort(1024); // size ??
        //Legend Info
        writeSizedString("Legend name");
        writeSizedString("Legend clan name");
        writeInt(1); // legend world id
        writeInt(0); // legend race
        writeInt(0); // legend sex
        writeInt(88); // legend class id
        writeInt(85); // legend level

        writeInt(5); // count
        writeInt(4); // win count
        writeInt(1); // lose count
        writeInt(100); // olympiad points
        writeInt(4); // clan level

        writeInt(40); // heroes size

        for (int i = 0; i < 40; i++) {
            writeSizedString("Hero name" + i);
            writeSizedString("Hero clan name" + i);
            writeInt((i % 2) + 1); // hero world id
            writeInt(0); // hero race
            writeInt(i % 2); // hero sex
            writeInt(88 + i); // hero class id
            writeInt(85); // hero level

            writeInt( (i % 4) + 1); // count
            writeInt(4 + i); // win count
            writeInt(1 + i); // lose count
            writeInt(100 + i); // olympiad points
            writeInt(5); // clan level
        }


    }
}
