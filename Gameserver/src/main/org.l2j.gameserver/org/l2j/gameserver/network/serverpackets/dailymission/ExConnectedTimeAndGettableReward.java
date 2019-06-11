package org.l2j.gameserver.network.serverpackets.dailymission;

import org.l2j.gameserver.data.xml.impl.DailyMissionData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExConnectedTimeAndGettableReward extends IClientOutgoingPacket {
    private final int _oneDayRewardAvailableCount;

    public ExConnectedTimeAndGettableReward(L2PcInstance player) {
        _oneDayRewardAvailableCount = DailyMissionData.getInstance().getDailyMissionData(player).size();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        if (!DailyMissionData.getInstance().isAvailable()) {
            return;
        }

        writeId(OutgoingPackets.EX_CONNECTED_TIME_AND_GETTABLE_REWARD);
        writeInt(0x00);
        writeInt(_oneDayRewardAvailableCount);
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
