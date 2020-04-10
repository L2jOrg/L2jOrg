package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.costume.ExSendCostumeListFull;

/**
 * @author JoeAlisson
 */
public class ExRequestCostumeList extends ClientPacket {

    private int type;

    @Override
    protected void readImpl() throws Exception {
        type = readInt();
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ExSendCostumeListFull());
    }
}
