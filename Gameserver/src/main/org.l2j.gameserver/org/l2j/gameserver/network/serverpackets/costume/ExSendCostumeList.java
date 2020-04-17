package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.data.database.data.CostumeData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;
import java.util.Set;

/**
 * @author JoeAlisson
 */
public class ExSendCostumeList extends ServerPacket {

    private final Collection<CostumeData> costumes;

    public ExSendCostumeList(CostumeData playerCostume) {
        costumes = Set.of(playerCostume);
    }

    public ExSendCostumeList(Set<CostumeData> costumes) {
        this.costumes = costumes;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_SEND_COSTUME_LIST);

        writeInt(costumes.size());
        for (var costume : costumes) {
            writeInt(costume.getId());
            writeLong(costume.getAmount());
            writeByte(costume.isLocked());
            writeByte(costume.checkIsNewAndChange());
        }
    }
}
