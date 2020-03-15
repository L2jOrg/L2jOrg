package org.l2j.gameserver.network.clientpackets.olympiad;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMyRankInfo;

/**
 * @author JoeAlisson
 */
public class ExRequestOlympiadMyRank  extends ClientPacket {

    @Override
    protected void readImpl() throws Exception {
        // dummy
    }

    @Override
    protected void runImpl()  {
        client.sendPacket(new ExOlympiadMyRankInfo());
    }

}
