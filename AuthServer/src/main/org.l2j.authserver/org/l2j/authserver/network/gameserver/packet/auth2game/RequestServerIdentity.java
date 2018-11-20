package org.l2j.authserver.network.gameserver.packet.auth2game;

public class RequestServerIdentity extends GameServerWritablePacket {
    @Override
    protected void writeImpl() {
        writeByte(0x06);
    }

    @Override
    protected int packetSize() {
        return super.packetSize() + 1;
    }
}
