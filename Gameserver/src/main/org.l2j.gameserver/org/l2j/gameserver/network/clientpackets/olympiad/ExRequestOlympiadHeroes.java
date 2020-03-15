package org.l2j.gameserver.network.clientpackets.olympiad;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadHeroesInfo;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadRankingInfo;

/**
 * @author JoeAlisson
 */
public class ExRequestOlympiadHeroes extends ClientPacket {

    @Override
    protected void readImpl() throws Exception {
        // trigger packet
    }

    @Override
    protected void runImpl()  {
        client.sendPacket(new ExOlympiadHeroesInfo());
    }

}
