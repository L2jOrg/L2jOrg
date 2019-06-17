package org.l2j.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Format: (ch) just a trigger
 *
 * @author -Wooden-
 */
public final class RequestExFishRanking extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestExFishRanking.class);

    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        LOGGER.info("C5: RequestExFishRanking");
    }
}