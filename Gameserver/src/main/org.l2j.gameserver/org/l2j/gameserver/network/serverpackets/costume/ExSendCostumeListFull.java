package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExSendCostumeListFull extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_SEND_COSTUME_LIST_FULL);
        var costumes = client.getPlayer().getCostumes();

        writeInt(costumes.size());
        for (var costume : costumes) {
            writeInt(costume.getId());
            writeLong(costume.getAmount());
            writeByte(costume.isLocked());
            writeByte(costume.checkIsNewAndChange());
        }

        writeInt(0); // shortcut disabled
        writeInt(0); // costume collection Id
        writeInt(0); // costume collection reuse cool time

    }
}
