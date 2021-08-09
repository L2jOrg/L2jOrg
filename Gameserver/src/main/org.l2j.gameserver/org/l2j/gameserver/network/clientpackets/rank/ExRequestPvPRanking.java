package org.l2j.gameserver.network.clientpackets.rank;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.rank.ExPvPRankingList;

public class ExRequestPvPRanking extends ClientPacket {
    private boolean currentSeason;
    private byte group;
    private byte scope;
    private int race;


    @Override
    protected void readImpl() throws Exception {
        currentSeason = readBoolean();
        group = readByte();
        scope = readByte();
        race = readInt();
    }


    @Override
    protected void runImpl() {
        client.sendPacket(new ExPvPRankingList(client.getPlayer(), currentSeason, group, scope, race));
    }
}
