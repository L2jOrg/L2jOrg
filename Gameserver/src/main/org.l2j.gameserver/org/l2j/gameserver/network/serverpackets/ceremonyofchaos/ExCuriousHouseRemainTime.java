package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_CURIOUS_HOUSE_REMAIN_TIME);
        writeInt(_time);
    }

}
