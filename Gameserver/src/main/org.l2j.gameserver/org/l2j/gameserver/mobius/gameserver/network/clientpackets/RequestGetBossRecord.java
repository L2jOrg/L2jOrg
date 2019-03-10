package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Format: (ch) d
 *
 * @author -Wooden-
 */
public class RequestGetBossRecord extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGetBossRecord.class);
    private int _bossId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _bossId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        LOGGER.warn("Player " + activeChar + " (boss ID: " + _bossId + ") used unsuded packet " + RequestGetBossRecord.class.getSimpleName());
    }
}