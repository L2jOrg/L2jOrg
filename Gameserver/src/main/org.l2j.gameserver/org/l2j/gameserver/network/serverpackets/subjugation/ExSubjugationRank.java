package org.l2j.gameserver.network.serverpackets.subjugation;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExSubjugationRank extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SUBJUGATION_RANKING, buffer);
        //buffer.writeInt(ranker.size); //todo rank size
        //  for (var ranker : rankers) {
            //  buffer.writeSizedString(); //todo UserName
            // buffer.writeInt(0); //todo ranker point
            // buffer.writeInt(0); //todo  ranker rank
        // }
        // buffer.writeInt(0); //todo id
        // buffer.writeInt(0); //todo point
        // buffer.writeInt(0); //todo rank
    }
}
