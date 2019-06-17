package org.l2j.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Format: (ch) S
 *
 * @author -Wooden- TODO: GodKratos: This packet is wrong in Gracia Final!!
 */
public final class RequestPCCafeCouponUse extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPCCafeCouponUse.class);
    private String _str;

    @Override
    public void readImpl() {
        _str = readString();
    }

    @Override
    public void runImpl() {
        LOGGER.info("C5: RequestPCCafeCouponUse: S: " + _str);
    }
}
