package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CURIOUS_HOUSE_REMAIN_TIME);
        writeInt(_time);
    }

}
