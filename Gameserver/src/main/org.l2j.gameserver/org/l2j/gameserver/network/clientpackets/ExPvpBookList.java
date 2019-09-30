package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.revenge.PvpBookList;

/**
 * @author JoeAlisson
 */
public class ExPvpBookList extends ClientPacket {

    @Override
    protected void readImpl() {
        // dummy byte
    }

    @Override
    protected void runImpl()  {
        client.sendPacket(new PvpBookList());
    }
}
