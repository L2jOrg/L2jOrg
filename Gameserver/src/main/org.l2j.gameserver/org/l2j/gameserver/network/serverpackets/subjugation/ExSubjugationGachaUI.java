package org.l2j.gameserver.network.serverpackets.subjugation;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExSubjugationGachaUI extends ServerPacket {
    public ExSubjugationGachaUI(Player player) {

    }
    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SUBJUGATION_GACHA_UI, buffer);
        // buffer.writeInt(); //todo nGachaPoint
    }
}
