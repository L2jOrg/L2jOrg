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
package org.l2j.gameserver.network.clientpackets.mission;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.mission.ExConnectedTimeAndGettableReward;
import org.l2j.gameserver.network.serverpackets.mission.ExOneDayReceiveRewardList;

import java.util.Collection;

/**
 * @author Sdw
 */
public class RequestOneDayRewardReceive extends ClientPacket {
    private int id;

    @Override
    public void readImpl() {
        id = readShort();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        var missionData = MissionData.getInstance();

        final Collection<MissionDataHolder> missions = missionData.getMissions(id);
        if (Util.isNullOrEmpty(missions)) { return;
        }

        missions.stream().filter(o -> o.isDisplayable(player)).forEach(r -> r.requestReward(player));

        player.sendPacket(new ExOneDayReceiveRewardList(player, true));
        player.sendPacket(new ExConnectedTimeAndGettableReward((int) missionData.getStoredMissionData(player).values().stream().filter(MissionPlayerData::isAvailable).count()));
    }
}
