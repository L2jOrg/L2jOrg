package org.l2j.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Format: (ch) S
 *
 * @author -Wooden- TODO: GodKratos: This packet is wrong in Gracia Final!!
 */
public final class RequestPCCafeCouponUse extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPCCafeCouponUse.class);
    private String _str;

    @Override
    public void readImpl(ByteBuffer packet) {
        _str = readString(packet);
    }

    @Override
    public void runImpl() {
        LOGGER.info("C5: RequestPCCafeCouponUse: S: " + _str);
    }
}
