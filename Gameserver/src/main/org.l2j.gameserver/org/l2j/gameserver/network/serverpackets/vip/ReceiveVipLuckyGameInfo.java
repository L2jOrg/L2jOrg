package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ReceiveVipLuckyGameInfo extends ServerPacket {

    @Override
    protected void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.RECEIVE_VIP_LUCKY_GAME_INFO);
        writeByte((byte) 1); //Enable 1
        writeInt((int) client.getActiveChar().getAdena());
        writeInt(client.getCoin());
    }

}
