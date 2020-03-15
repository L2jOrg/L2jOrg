package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExOlympiadMyRankInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_OLYMPIAD_MY_RANKING_INFO);
        writeInt(2020); // season year
        writeInt(3); // season month
        writeInt(1); // season
        writeInt(2); // rank
        writeInt(5); // win count
        writeInt(2); // lose count
        writeInt(100); // points

        writeInt(3); // prev rank
        writeInt(8); // prev win count
        writeInt(1); // prev lose count
        writeInt(150); // prev points

        writeInt(5); // hero count
        writeInt(2); // legend count

        writeInt(3); // recent matches count

        for (int i = 0; i < 3; i++) {
            writeSizedString("Enemy" + i); // enemy name
            writeByte(i %2 == 0); // lost ?
            writeInt(75 + i); // enemy level
            writeInt(88 + i); // enemy class
        }
    }
}
