package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExCuriousHouseRemainTime extends ServerPacket {
    private final int _time;

    public ExCuriousHouseRemainTime(int time) {
        _time = time;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_CURIOUS_HOUSE_REMAIN_TIME);
        writeInt(_time);
    }

}
