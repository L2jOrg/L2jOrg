package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExOlympiadRecord extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_OLYMPIAD_RECORD);

        writeInt(10); // points
        writeInt(3); // win count
        writeInt(0); // lose count
        writeInt(3); // match left (MAX 5)

        // From olympiad history
        writeInt(client.getPlayer().getClassId().getId()); // prev class type

        writeInt(4);  // prev rank (non classed all servers)
        writeInt(100); // prev rank count

        writeInt(2); // prev class rank
        writeInt(30); // prev class rank count

        writeInt(2); // prev class rank by server
        writeInt(15); // prev class rank by server count

        writeInt(110); // prev point
        writeInt(12); // prev win count
        writeInt(3); // prev lose count

        writeInt(1); // prev grade
        writeInt(2020); // season year
        writeInt(3); // season month
        writeByte(false); // match open
        writeInt(1); // season
        writeByte(false); // registered
        writeByte(3); // game rule type (0 - 3v3)
    }
}
