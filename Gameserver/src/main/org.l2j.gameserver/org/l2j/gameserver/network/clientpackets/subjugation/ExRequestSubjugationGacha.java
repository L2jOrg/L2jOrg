package org.l2j.gameserver.network.clientpackets.subjugation;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.subjugation.ExSubjugationGacha;

public class ExRequestSubjugationGacha extends ClientPacket {
    @Override
    protected void readImpl() throws Exception {
        // dummy byte
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ExSubjugationGacha(client.getPlayer()));
    }
}