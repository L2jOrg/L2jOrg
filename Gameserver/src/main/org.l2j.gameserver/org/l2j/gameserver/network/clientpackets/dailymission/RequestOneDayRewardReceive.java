package org.l2j.gameserver.network.clientpackets.dailymission;

import org.l2j.gameserver.data.xml.impl.DailyMissionData;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.dailymission.ExConnectedTimeAndGettableReward;
import org.l2j.gameserver.network.serverpackets.dailymission.ExOneDayReceiveRewardList;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author Sdw
 */
public class RequestOneDayRewardReceive extends IClientIncomingPacket {
    private int _id;

    @Override
    public void readImpl(ByteBuffer packet) {
        _id = packet.getShort();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        final Collection<DailyMissionDataHolder> reward = DailyMissionData.getInstance().getDailyMissionData(_id);
        if ((reward == null) || reward.isEmpty()) {
            return;
        }

        reward.stream().filter(o -> o.isDisplayable(player)).forEach(r -> r.requestReward(player));
        player.sendPacket(new ExConnectedTimeAndGettableReward(player));
        player.sendPacket(new ExOneDayReceiveRewardList(player, true));
    }
}
