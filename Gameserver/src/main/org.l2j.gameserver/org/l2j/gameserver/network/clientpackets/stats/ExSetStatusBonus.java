package org.l2j.gameserver.network.clientpackets.stats;

import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * @author JoeAlisson
 */
public class ExSetStatusBonus extends ClientPacket {

    private short str;
    private short dex;
    private short con;
    private short intt;
    private short wit;
    private short men;

    @Override
    protected void readImpl() throws Exception {
        readShort();
        readShort();
        str = readShort();
        dex = readShort();
        con = readShort();
        intt = readShort();
        wit = readShort();
        men = readShort();
    }

    @Override
    protected void runImpl() throws Exception {

    }
}
