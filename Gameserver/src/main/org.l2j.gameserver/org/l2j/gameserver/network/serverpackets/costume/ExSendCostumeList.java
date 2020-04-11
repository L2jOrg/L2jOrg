package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExSendCostumeList extends ServerPacket {
    
    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_SEND_COSTUME_LIST);
        var costumes = client.getPlayer().getCostumes();
        writeInt(costumes.size());
        for (var costume : costumes) {
            writeInt(costume.getId());
            writeLong(costume.getAmount());
            writeByte(0); // lock state
            writeByte(0); // changed type
        }
    }
}
