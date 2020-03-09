package org.l2j.gameserver.network.clientpackets.pvpbook;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.pvpbook.PvpBookList;

/**
 * @author JoeAlisson
 */
public class ExRequestPvpBookList extends ClientPacket {

    @Override
    protected void readImpl() {
        // dummy byte
    }

    @Override
    protected void runImpl()  {
        client.sendPacket(new PvpBookList());
    }
}
