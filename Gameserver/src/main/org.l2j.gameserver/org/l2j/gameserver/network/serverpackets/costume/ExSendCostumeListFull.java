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
            writeLong(costume.getAmount()); // amount
            writeByte(costume.isLocked()); // lock  state ?
            writeByte(true); // new
        }


        var size = 0; // shortcut list size
        writeInt(size);
        for (int i = 0; i < size; i++) {
            writeInt(1); // page
            writeInt(i); // slot index
            writeInt(i + 1); // costume id
            writeByte(false); // auto use
        }

        writeInt(0); // costume collection Id
        writeInt(0); // costume collection reuse cool time

    }
}
