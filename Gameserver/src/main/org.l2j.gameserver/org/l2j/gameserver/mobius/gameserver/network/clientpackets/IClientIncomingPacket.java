package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.gameserver.mobius.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Packets received by the game server from clients
 * @author KenM
 */
public abstract class IClientIncomingPacket extends ReadablePacket<L2GameClient> {

    private static Logger LOGGER = LoggerFactory.getLogger(IClientIncomingPacket.class.getName());

    @Override
    protected boolean read(ByteBuffer packet) {
        try {
            readImpl(packet);
            return true;
        } catch (InvalidDataPacketException e) {
            LOGGER.warn("Invalid data packet {} from client {}", this, client);
        }
        catch (Exception e) {
            LOGGER.error("Error while reading packet {} from client {}", this, client);
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return false;
    }


    @Override
    public void run() {
        try {
            runImpl();
        } catch (Exception e) {
            LOGGER.error("Error while running packet {} from client {}", this, client);
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    protected abstract void runImpl() throws Exception;

    protected abstract void readImpl(ByteBuffer packet) throws Exception;
}
