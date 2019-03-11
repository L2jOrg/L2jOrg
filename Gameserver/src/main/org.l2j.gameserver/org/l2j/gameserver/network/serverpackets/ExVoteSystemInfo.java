package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * ExVoteSystemInfo packet implementation.
 *
 * @author Gnacik
 */
public class ExVoteSystemInfo extends IClientOutgoingPacket {
    private final int _recomLeft;
    private final int _recomHave;
    private final int _bonusTime;
    private final int _bonusVal;
    private final int _bonusType;

    public ExVoteSystemInfo(L2PcInstance player) {
        _recomLeft = player.getRecomLeft();
        _recomHave = player.getRecomHave();
        _bonusTime = 0;
        _bonusVal = 0;
        _bonusType = 0;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_VOTE_SYSTEM_INFO.writeId(packet);

        packet.putInt(_recomLeft);
        packet.putInt(_recomHave);
        packet.putInt(_bonusTime);
        packet.putInt(_bonusVal);
        packet.putInt(_bonusType);
    }
}
