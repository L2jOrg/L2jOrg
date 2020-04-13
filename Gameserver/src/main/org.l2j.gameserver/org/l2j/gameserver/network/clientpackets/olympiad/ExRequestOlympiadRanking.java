package org.l2j.gameserver.network.clientpackets.olympiad;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadRankingInfo;

/**
 * @author JoeAlisson
 */
public class ExRequestOlympiadRanking extends ClientPacket {

    private byte type;
    private byte scope;
    private boolean currentSeason;
    private int classId;
    private int worldId;

    @Override
    protected void readImpl() throws Exception {
        type = readByte(); // 0 - Server; 1 -> Class
        scope = readByte(); // 0 - top;  1 - My Rank
        currentSeason = readBoolean();
        classId = readInt();
        worldId = readInt(); // 0 - all
    }

    @Override
    protected void runImpl()  {
        client.sendPacket(new ExOlympiadRankingInfo());
    }

}
