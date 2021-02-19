package org.l2j.gameserver.network.serverpackets.subjugation;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.rank.RankEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExSubjugationGacha extends ServerPacket {

    public ExSubjugationGacha(Player player) {

    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SUBJUGATION_GACHA, buffer);
        //buffer.writeInt(gachaItem.size); //todo gachasize
        //  for (var gachaItem : gachas) {
               // buffer.writeInt(); //todo classId
              //  buffer.writeInt(); //todo Amount
        // }
    }
}
