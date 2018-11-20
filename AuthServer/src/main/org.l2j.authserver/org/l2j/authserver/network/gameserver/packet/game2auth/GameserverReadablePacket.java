package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.network.gameserver.ServerClient;
import org.l2j.mmocore.ReadablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameserverReadablePacket extends ReadablePacket<ServerClient> {

    private static final Logger logger = LoggerFactory.getLogger(GameserverReadablePacket.class);

    @Override
    protected boolean read() {
        try {
            readImpl();
        } catch (Exception e) {
            logger.error("Reading {} : {} ", getClass().getSimpleName(), e);
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        try {
            runImpl();
        } catch (Exception e) {
            logger.error("Running {} : {} ", getClass().getSimpleName(), e);
        }
    }

    protected abstract void readImpl() throws  Exception;
    protected abstract void runImpl() throws Exception;
}
