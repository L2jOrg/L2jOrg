package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.L2LoginServerPacket;

import java.nio.ByteBuffer;

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
    protected void writeImpl(AuthClient client, ByteBuffer buffer) {
        buffer.put((byte)0x00);

        buffer.putInt(client.getSessionId());
        buffer.putInt(0xc621);

        buffer.put(client.getScrambledModulus());

        // unk GG related?
        buffer.putInt(0x29DD954E);
        buffer.putInt(0x77C39CFC);
        buffer.putInt(0x97ADB620);
        buffer.putInt(0x07BDE0F7);

        buffer.put(client.getBlowfishKey());
        buffer.putInt(0x00);
    }

    @Override
    protected int size(AuthClient client) {
        return super.size(client) + 175;
    }
}
