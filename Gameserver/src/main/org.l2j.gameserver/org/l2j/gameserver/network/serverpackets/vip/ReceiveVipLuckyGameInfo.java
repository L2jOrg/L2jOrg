package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ReceiveVipLuckyGameInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_LUCKY_GAME_INFO);
        writeByte((byte) 1); //Enable 1
        writeInt((int) client.getPlayer().getAdena());
        writeInt(client.getCoin());
    }

}
