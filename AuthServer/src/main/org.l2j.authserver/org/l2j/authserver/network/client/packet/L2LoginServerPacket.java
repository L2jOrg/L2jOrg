package org.l2j.authserver.network.client.packet;

import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.authserver.network.client.AuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KenM
 */
public abstract class L2LoginServerPacket extends WritablePacket<AuthClient> {

    private static final Logger logger = LoggerFactory.getLogger(L2LoginServerPacket.class);

    @Override
    protected boolean write(AuthClient client) {
        try {
            writeImpl(client);
            return true;
        } catch (Exception e) {
           logger.error(e.getLocalizedMessage(),e);
        }
        return false;
    }

    protected abstract void writeImpl(AuthClient client);
}
