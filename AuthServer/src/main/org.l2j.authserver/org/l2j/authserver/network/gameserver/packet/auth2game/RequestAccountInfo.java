package org.l2j.authserver.network.gameserver.packet.auth2game;

public class RequestAccountInfo extends GameServerWritablePacket {

    private final String account;

    public RequestAccountInfo(String account) {
        this.account = account;
    }

    @Override
    protected void writeImpl() {
        writeByte(0x05);
        writeString(account);
    }

    @Override
    protected int packetSize() {
        return super.packetSize() + 3 + 2 * account.length();
    }
}
