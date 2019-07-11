package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Format: (ch) d
 *
 * @author -Wooden-
 */
public class RequestGetBossRecord extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGetBossRecord.class);
    private int _bossId;

    @Override
    public void readImpl() {
        _bossId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        LOGGER.warn("Player " + activeChar + " (boss ID: " + _bossId + ") used unsuded packet " + RequestGetBossRecord.class.getSimpleName());
    }
}