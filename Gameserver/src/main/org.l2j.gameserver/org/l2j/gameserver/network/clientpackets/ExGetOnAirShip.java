package org.l2j.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Format: (c) dddd d: dx d: dy d: dz d: AirShip id ??
 *
 * @author -Wooden-
 */
public class ExGetOnAirShip extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExGetOnAirShip.class);

    private int _x;
    private int _y;
    private int _z;
    private int _shipId;

    @Override
    public void readImpl() {
        _x = readInt();
        _y = readInt();
        _z = readInt();
        _shipId = readInt();
    }

    @Override
    public void runImpl() {
        LOGGER.info("[T1:ExGetOnAirShip] x: " + _x);
        LOGGER.info("[T1:ExGetOnAirShip] y: " + _y);
        LOGGER.info("[T1:ExGetOnAirShip] z: " + _z);
        LOGGER.info("[T1:ExGetOnAirShip] ship ID: " + _shipId);
    }
}
