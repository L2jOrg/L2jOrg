package org.l2j.gameserver.network.serverpackets.mission;

import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExConnectedTimeAndGettableReward extends ServerPacket {
    private final int oneDayRewardAvailableCount;

    public ExConnectedTimeAndGettableReward(Player player) {
        oneDayRewardAvailableCount = MissionData.getInstance().getAvailableMissionCount(player);
    }

    public ExConnectedTimeAndGettableReward(int availableCount) {
        oneDayRewardAvailableCount = availableCount;
    }

    @Override
    public void writeImpl(GameClient client) {
        if (!MissionData.getInstance().isAvailable()) {
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
