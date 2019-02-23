package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.network.gameserver.ServerClient;
import io.github.joealisson.mmocore.ReadablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public abstract class GameserverReadablePacket extends ReadablePacket<ServerClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameserverReadablePacket.class);

    @Override
    protected boolean read(ByteBuffer buffer) {
        try {
            readImpl(buffer);
        } catch (Exception e) {
            LOGGER.error("Reading {} : {} ", getClass().getSimpleName(), e);
            LOGGER.error(e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        try {
            runImpl();
        } catch (Exception e) {
            LOGGER.error("Running {} : {} ", getClass().getSimpleName(), e);
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    protected abstract void readImpl(ByteBuffer buffer) throws  Exception;
    protected abstract void runImpl() throws Exception;
}
