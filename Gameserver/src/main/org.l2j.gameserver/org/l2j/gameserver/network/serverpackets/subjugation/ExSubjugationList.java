package org.l2j.gameserver.network.serverpackets.subjugation;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExSubjugationList extends ServerPacket {
    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SUBJUGATION_LIST, buffer );
        //buffer.writeInt(list size); //todo  size
        //  for (character : listsize) {
            //buffer.writeInt(); //todo ID
           // buffer.writeInt();  //todo Point
           // buffer.writeInt(); //todo Gachapoint
           // buffer.writeInt(); //todo RemainPeriodicGachaPoint
       // }
    }
}
