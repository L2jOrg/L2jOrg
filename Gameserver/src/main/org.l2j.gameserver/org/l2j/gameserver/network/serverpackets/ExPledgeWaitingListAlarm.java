package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExPledgeWaitingListAlarm extends ServerPacket {
    public static final ExPledgeWaitingListAlarm STATIC_PACKET = new ExPledgeWaitingListAlarm();

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_WAITING_LIST_ALARM);

    }

}
