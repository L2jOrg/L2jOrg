package org.l2j.gameserver.network.serverpackets.dailymission;

import org.l2j.gameserver.data.xml.impl.DailyMissionData;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2j.gameserver.util.cron4j.Predictor;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * @author Sdw
 */
public class ExOneDayReceiveRewardList extends IClientOutgoingPacket {
    private static final Function<String, Long> _remainTime = pattern -> (new Predictor(pattern).nextMatchingTime() - System.currentTimeMillis()) / 1000;
    private final L2PcInstance _player;
    private final Collection<DailyMissionDataHolder> _rewards;
    private final long _dayRemainTime;
    private final long _weekRemainTime;
    private final long _monthRemainTime;

    public ExOneDayReceiveRewardList(L2PcInstance player, boolean sendRewards) {
        _player = player;
        _rewards = sendRewards ? DailyMissionData.getInstance().getDailyMissionData(player) : Collections.emptyList();
        _dayRemainTime = _remainTime.apply("30 6 * * *");
        _weekRemainTime = _remainTime.apply("30 6 * * 1");
        _monthRemainTime = _remainTime.apply("30 6 1 * *");
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        if (!DailyMissionData.getInstance().isAvailable()) {
            return;
        }

        OutgoingPackets.EX_ONE_DAY_RECEIVE_REWARD_LIST.writeId(packet);

        packet.putInt((int) _dayRemainTime);
        packet.putInt((int) _weekRemainTime);
        packet.putInt((int) _monthRemainTime);
        packet.put((byte) 0x17);
        packet.putInt(_player.getClassId().getId());
        packet.putInt(LocalDate.now().getDayOfWeek().ordinal()); // Day of week
        packet.putInt(_rewards.size());
        for (DailyMissionDataHolder reward : _rewards) {
            packet.putShort((short) reward.getId());
            packet.put((byte) reward.getStatus(_player));
            packet.put((byte) (reward.getRequiredCompletions() > 1 ? 0x01 : 0x00));
            packet.putInt(Math.min(reward.getProgress(_player), _player.getLevel()));
            packet.putInt(reward.getRequiredCompletions());
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 30 + _rewards.size() * 12;
    }
}
