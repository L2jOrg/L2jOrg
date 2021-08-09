package org.l2j.gameserver.network.clientpackets.subjugation;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.subjugation.ExSubjugationGachaUI;

public class ExRequestSubjugationGachaUI extends ClientPacket {
    @Override
    protected void readImpl() throws Exception {
        // dummy byte
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ExSubjugationGachaUI(client.getPlayer()));

    }
}