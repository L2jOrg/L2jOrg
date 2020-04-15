package org.l2j.gameserver.network.clientpackets.stats;

import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.UserInfo;

/**
 * @author JoeAlisson
 */
public class ExResetStatusBonus extends ClientPacket {

    @Override
    protected void readImpl() throws Exception {

    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        if(player.reduceAdena("Reset Stats", 2900000, player, true)) {
            player.getStatsData().reset();
            client.sendPacket(new UserInfo(player, UserInfoType.STATS,  UserInfoType.STATS_POINTS, UserInfoType.BASE_STATS ));
            player.getStats().recalculateStats(true);
        }
    }
}
