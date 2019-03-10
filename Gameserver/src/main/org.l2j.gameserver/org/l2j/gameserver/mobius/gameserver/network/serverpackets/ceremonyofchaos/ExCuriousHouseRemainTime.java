package org.l2j.gameserver.mobius.gameserver.network.serverpackets.ceremonyofchaos;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExCuriousHouseRemainTime extends IClientOutgoingPacket {
    private final int _time;

    public ExCuriousHouseRemainTime(int time) {
        _time = time;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CURIOUS_HOUSE_REMAIN_TIME.writeId(packet);
        packet.putInt(_time);

    }
}
