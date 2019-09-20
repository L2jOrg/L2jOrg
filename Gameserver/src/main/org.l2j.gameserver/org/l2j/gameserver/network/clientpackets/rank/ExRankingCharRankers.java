package org.l2j.gameserver.network.clientpackets.rank;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.rank.ExRankList;

/**
 * @author JoeAlisson
 */
public class ExRankingCharRankers extends ClientPacket {

    private byte group;
    private byte scope;
    private int race;

    @Override
    protected void readImpl() throws Exception {
        group = readByte();
        scope = readByte();
        race = readInt();

    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ExRankList(group, scope, race));
    }
}
