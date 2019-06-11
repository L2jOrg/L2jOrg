package org.l2j.authserver.network.gameserver.packet.game2auth;

import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.authserver.network.gameserver.ServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameserverReadablePacket extends ReadablePacket<ServerClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameserverReadablePacket.class);

    @Override
    protected boolean read() {
        try {
            readImpl();
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

    protected abstract void readImpl() throws  Exception;
    protected abstract void runImpl() throws Exception;
}
