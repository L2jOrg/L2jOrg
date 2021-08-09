package org.l2j.gameserver.network.clientpackets.subjugation;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.subjugation.ExSubjugationList;
import org.l2j.gameserver.network.serverpackets.subjugation.ExSubjugationRank;

public class ExRequestSubjugationList extends ClientPacket {
    @Override
    protected void readImpl() throws Exception {
        // dummy byte
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ExSubjugationList());
    }
}
