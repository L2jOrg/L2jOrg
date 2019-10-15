package org.l2j.gameserver.network.clientpackets.mission;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
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
