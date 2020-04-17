package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.data.database.data.CostumeData;
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
        var player = client.getPlayer();

        writeInt(player.getCostumeAmount());
        player.forEachCostume(this::writeCostume);

        writeInt(0); // shortcut disabled

        var activeCollection  = player.getActiveCostumeCollection();
        writeInt(activeCollection.getId());
        writeInt(activeCollection.getReuseTime());
    }

    private void writeCostume(CostumeData costume) {
        writeInt(costume.getId());
        writeLong(costume.getAmount());
        writeByte(costume.isLocked());
        writeByte(costume.checkIsNewAndChange());
    }
}
