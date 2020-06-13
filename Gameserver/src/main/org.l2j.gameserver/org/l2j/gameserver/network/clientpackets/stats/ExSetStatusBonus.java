/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
