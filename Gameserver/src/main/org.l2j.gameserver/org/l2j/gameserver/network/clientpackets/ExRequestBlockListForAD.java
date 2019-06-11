package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.model.BlockList;

import java.nio.ByteBuffer;

public class ExRequestBlockListForAD extends IClientIncomingPacket {

    private String name;
    private String message;

    @Override
    protected void readImpl() throws Exception {
        name = readString();
        message = readString();
        // next is Always Adena Sale ADS text
    }

    @Override
    protected void runImpl() {
        // simple check if message contains adena. Should have some others checks or not check at all?!
        if(!message.toLowerCase().contains("adena")) {
            return;
        }

        var player = client.getActiveChar();
        final int reportedId = CharNameTable.getInstance().getIdByName(name);
        BlockList.addToBlockList(player, reportedId);

        ReportTable.getInstance().reportAdenaADS(player.getObjectId(), reportedId);
    }
}
