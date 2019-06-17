package org.l2j.gameserver.network.serverpackets.dailymission;

import org.l2j.gameserver.data.xml.impl.DailyMissionData;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.cron4j.Predictor;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * @author Sdw
 */
public class ExOneDayReceiveRewardList extends ServerPacket {
    private static final Function<String, Long> remainTime = pattern -> (new Predictor(pattern).nextMatchingTime() - System.currentTimeMillis()) / 1000;
    private final L2PcInstance player;
    private final Collection<DailyMissionDataHolder> missions;
    private final long dayRemainTime;
    private final long weekRemainTime;
    private final long monthRemainTime;

    public ExOneDayReceiveRewardList(L2PcInstance player, boolean sendRewards) {
        this.player = player;
        missions = sendRewards ? DailyMissionData.getInstance().getDailyMissionData(player) : Collections.emptyList();
        dayRemainTime = remainTime.apply("30 6 * * *");
        weekRemainTime = remainTime.apply("30 6 * * 1");
        monthRemainTime = remainTime.apply("30 6 1 * *");
    }

    @Override
    public void writeImpl(L2GameClient client) {
        if (!DailyMissionData.getInstance().isAvailable()) {
            return;
        }

        writeId(ServerPacketId.EX_ONE_DAY_RECEIVE_REWARD_LIST);

        writeInt((int) dayRemainTime);
        writeInt((int) weekRemainTime);
        writeInt((int) monthRemainTime);
        writeByte(0x17);
        writeInt(player.getClassId().getId());
        writeInt(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)); // Day of week
        writeInt(missions.size());
        for (DailyMissionDataHolder mission : missions) {
            writeShort(mission.getId());
            writeByte(mission.getStatus(player));
            writeByte((mission.getRequiredCompletions() > 1));
            writeInt(Math.min(mission.getProgress(player), player.getLevel()));
            writeInt(mission.getRequiredCompletions());
        }
    }

}
