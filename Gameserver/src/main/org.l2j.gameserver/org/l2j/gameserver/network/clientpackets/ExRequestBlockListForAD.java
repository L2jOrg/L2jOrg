package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.model.BlockList;

public class ExRequestBlockListForAD extends ClientPacket {

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

        var player = client.getPlayer();
        final int reportedId = PlayerNameTable.getInstance().getIdByName(name);
        BlockList.addToBlockList(player, reportedId);

        ReportTable.getInstance().reportAdenaADS(player.getObjectId(), reportedId);
    }
}
