package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

public class ReceiveVipLuckyGameInfo extends IClientOutgoingPacket {

    @Override
    protected void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.RECEIVE_VIP_LUCKY_GAME_INFO);
        writeByte((byte) 1); //Enable 1
        writeInt((int) client.getActiveChar().getAdena());
        writeInt(client.getCoin());
    }

}
