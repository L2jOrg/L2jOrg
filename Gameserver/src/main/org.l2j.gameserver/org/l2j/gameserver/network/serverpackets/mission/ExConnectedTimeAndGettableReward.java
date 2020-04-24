package org.l2j.gameserver.network.serverpackets.mission;

import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
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

        writeId(ServerExPacketId.EX_ONE_DAY_REWARD_INFO);
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
