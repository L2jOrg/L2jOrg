package org.l2j.gameserver.network.serverpackets.revenge;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class PvpBookList extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_PVPBOOK_LIST);
        var size = 1;
        writeInt(4); // show killer's location count
        writeInt(5); // teleport count

        writeInt(size); // killer count

        for (int i = 0; i < size; i++) {
            writeSizedString("killer" + i); // killer name
            writeSizedString("clanKiller" + i); // killer clan name
            writeInt(15); // killer level
            writeInt(2); // killer race
            writeInt(10); // killer class
            writeInt((int) LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()); // kill time
            writeByte(true); // is online
        }
    }
}
