package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.L2LoginServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Fromat: d d: response
 */
public final class GGAuth extends L2LoginServerPacket {
    static final Logger logger = LoggerFactory.getLogger(GGAuth.class);
    public static final int SKIP_GG_AUTH_REQUEST = 0x0b;

    private final int _response;

    public GGAuth(int response) {
        _response = response;
        logger.debug("Reason Hex: {}", Integer.toHexString(response));
    }

    @Override
    protected void writeImpl(AuthClient client, ByteBuffer buffer) {
        buffer.put((byte)0x0b);
        buffer.putInt(_response);
        buffer.putInt(0x00);
        buffer.putInt(0x00);
        buffer.putInt(0x00);
        buffer.putInt(0x00);
    }

    @Override
    protected int size(AuthClient client) {
        return super.size(client) + 21;
    }
}
