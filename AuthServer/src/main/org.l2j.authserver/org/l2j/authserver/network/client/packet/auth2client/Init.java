package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.network.client.packet.L2LoginServerPacket;

/**
 * Format: dd b dddd s
 * d: session id
 * d: protocol revision
 * b: 0x90 bytes : 0x80 bytes for the scrambled RSA public key
 * 0x10 bytes at 0x00
 * d: unknow
 * d: unknow
 * d: unknow
 * d: unknow
 * s: blowfish key
 */
public final class Init extends L2LoginServerPacket {

    @Override
    protected void write() {
        writeByte(0x00);

        writeInt(client.getSessionId());
        writeInt(0xc621);

        writeBytes(client.getScrambledModulus());

        // unk GG related?
        writeInt(0x29DD954E);
        writeInt(0x77C39CFC);
        writeInt(0x97ADB620);
        writeInt(0x07BDE0F7);

        writeBytes(client.getBlowfishKey());
        writeInt(0x00);
    }

    @Override
    protected int packetSize() {
        return super.packetSize() + 175;
    }
}
