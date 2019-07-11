package org.l2j.gameserver.network.serverpackets.dailymission;

import org.l2j.gameserver.data.xml.impl.DailyMissionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExConnectedTimeAndGettableReward extends ServerPacket {
    private final int oneDayRewardAvailableCount;

    public ExConnectedTimeAndGettableReward(Player player) {
        oneDayRewardAvailableCount = DailyMissionData.getInstance().getAvailableDailyMissionCount(player);
    }

    public ExConnectedTimeAndGettableReward(int availableCount) {
        oneDayRewardAvailableCount = availableCount;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        if (!DailyMissionData.getInstance().isAvailable()) {
            return;
        }

        writeId(ServerPacketId.EX_CONNECTED_TIME_AND_GETTABLE_REWARD);
        writeInt(0x00);
        writeInt(oneDayRewardAvailableCount);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
    }

}
