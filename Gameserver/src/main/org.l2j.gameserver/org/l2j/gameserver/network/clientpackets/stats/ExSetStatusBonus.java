package org.l2j.gameserver.network.clientpackets.stats;

import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.UserInfo;

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
        var unk = readShort();
        var unk1 = readShort();
        str = readShort();
        dex = readShort();
        con = readShort();
        intt = readShort();
        wit = readShort();
        men = readShort();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        var statsData = player.getStatsData();
        if(statsData.update(str, dex, con, intt, wit, men)) {
            client.sendPacket(new UserInfo(player, UserInfoType.STATS, UserInfoType.STATS_POINTS, UserInfoType.BASE_STATS ));
            player.getStats().recalculateStats(true);
        }
    }
}
