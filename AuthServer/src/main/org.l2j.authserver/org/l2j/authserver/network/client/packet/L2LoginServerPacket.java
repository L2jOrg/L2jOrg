package org.l2j.authserver.network.client.packet;

import org.l2j.authserver.network.client.AuthClient;
import io.github.joealisson.mmocore.WritablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public abstract class L2LoginServerPacket extends WritablePacket<AuthClient> {

    private static final Logger logger = LoggerFactory.getLogger(L2LoginServerPacket.class);

    @Override
    protected boolean write(AuthClient client, ByteBuffer buffer) {
        try {
            writeImpl(client, buffer);
            return true;
        } catch (Exception e) {
           logger.error(e.getLocalizedMessage(),e);
        }
        return false;
    }

    protected abstract void writeImpl(AuthClient client, ByteBuffer buffer);

    @Override
    protected int size(AuthClient client) {
        return 22; // HEADER + CHECKSUM + PADDING
    }
}
