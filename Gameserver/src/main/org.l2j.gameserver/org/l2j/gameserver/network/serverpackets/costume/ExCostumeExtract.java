package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.data.database.data.CostumeData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExCostumeExtract extends ServerPacket {

    private int costumeId;
    private boolean success;
    private long amount;
    private int extractedItem;
    private long totalAmount;

    private ExCostumeExtract() {
    }

    public static ExCostumeExtract failed(int costumeId) {
        var packet = new ExCostumeExtract();
        packet.costumeId = costumeId;
        return packet;
    }

    public static ExCostumeExtract success(CostumeData costume, int extractItem, long amount) {
        var packet = new ExCostumeExtract();
        packet.costumeId = costume.getId();
        packet.success = true;
        packet.extractedItem = extractItem;
        packet.amount = amount;
        packet.totalAmount = costume.getAmount();
        return packet;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_COSTUME_EXTRACT);
        writeByte(success);
        writeInt(costumeId);
        writeLong(amount);
        writeInt(extractedItem);
        writeLong(amount);
        writeLong(totalAmount);
    }
}
