package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.DoorData;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.serverpackets.GetOnVehicle;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class ...
 *
 * @version $Revision: 1.13.4.7 $ $Date: 2005/03/27 15:29:30 $
 */
public class ValidatePosition extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatePosition.class);
    private int _x;
    private int _y;
    private int _z;
    private int _heading;
    private int _data; // vehicle id

    @Override
    public void readImpl() {
        _x = readInt();
        _y = readInt();
        _z = readInt();
        _heading = readInt();
        _data = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if ((activeChar == null) || activeChar.isTeleporting() || activeChar.inObserverMode()) {
            return;
        }

        final int realX = activeChar.getX();
        final int realY = activeChar.getY();
        int realZ = activeChar.getZ();

        if (Config.DEVELOPER) {
            LOGGER.debug("client pos: " + _x + " " + _y + " " + _z + " head " + _heading);
            LOGGER.debug("server pos: " + realX + " " + realY + " " + realZ + " head " + activeChar.getHeading());
        }

        if ((_x == 0) && (_y == 0)) {
            if (realX != 0) {
                return;
            }
        }

        int dx;
        int dy;
        int dz;
        double diffSq;

        if (activeChar.isInBoat()) {
            if (Config.COORD_SYNCHRONIZE == 2) {
                dx = _x - activeChar.getInVehiclePosition().getX();
                dy = _y - activeChar.getInVehiclePosition().getY();
                // dz = _z - activeChar.getInVehiclePosition().getZ();
                diffSq = ((dx * dx) + (dy * dy));
                if (diffSq > 250000) {
                    client.sendPacket(new GetOnVehicle(activeChar.getObjectId(), _data, activeChar.getInVehiclePosition()));
                }
            }
            return;
        }
        if (activeChar.isInAirShip()) {
            // Zoey76: TODO: Implement or cleanup.
            // if (Config.COORD_SYNCHRONIZE == 2)
            // {
            // dx = _x - activeChar.getInVehiclePosition().getX();
            // dy = _y - activeChar.getInVehiclePosition().getY();
            // dz = _z - activeChar.getInVehiclePosition().getZ();
            // diffSq = ((dx * dx) + (dy * dy));
            // if (diffSq > 250000)
            // {
            // sendPacket(new GetOnVehicle(activeChar.getObjectId(), _data, activeChar.getInBoatPosition()));
            // }
            // }
            return;
        }

        if (activeChar.isFalling(_z)) {
            return; // disable validations during fall to avoid "jumping"
        }

        dx = _x - realX;
        dy = _y - realY;
        dz = _z - realZ;
        diffSq = ((dx * dx) + (dy * dy));

        // Zoey76: TODO: Implement or cleanup.
        // L2Party party = activeChar.getParty();
        // if ((party != null) && (activeChar.getLastPartyPositionDistance(_x, _y, _z) > 150))
        // {
        // activeChar.setLastPartyPosition(_x, _y, _z);
        // party.broadcastToPartyMembers(activeChar, new PartyMemberPosition(activeChar));
        // }

        // Don't allow flying transformations outside gracia area!
        if (activeChar.isFlyingMounted() && (_x > L2World.GRACIA_MAX_X)) {
            activeChar.untransform();
        }

        if (activeChar.isFlying() || activeChar.isInsideZone(ZoneId.WATER)) {
            activeChar.setXYZ(realX, realY, _z);
            if (diffSq > 90000) {
                activeChar.sendPacket(new ValidateLocation(activeChar));
            }
        } else if (diffSq < 360000) // if too large, messes observation
        {
            if (Config.COORD_SYNCHRONIZE == -1) // Only Z coordinate synched to server,
            // mainly used when no geodata but can be used also with geodata
            {
                activeChar.setXYZ(realX, realY, _z);
                return;
            }
            if (Config.COORD_SYNCHRONIZE == 1) // Trusting also client x,y coordinates (should not be used with geodata)
            {
                if (!activeChar.isMoving() || !activeChar.validateMovementHeading(_heading)) // Heading changed on client = possible obstacle
                {
                    // character is not moving, take coordinates from client
                    if (diffSq < 2500) {
                        activeChar.setXYZ(realX, realY, _z);
                    } else {
                        activeChar.setXYZ(_x, _y, _z);
                    }
                } else {
                    activeChar.setXYZ(realX, realY, _z);
                }

                activeChar.setHeading(_heading);
                return;
            }
            // Sync 2 (or other),
            // intended for geodata. Sends a validation packet to client
            // when too far from server calculated true coordinate.
            // Due to geodata/zone errors, some Z axis checks are made. (maybe a temporary solution)
            // Important: this code part must work together with L2Character.updatePosition
            if ((diffSq > 250000) || (Math.abs(dz) > 200)) {
                // if ((_z - activeChar.getClientZ()) < 200 && Math.abs(activeChar.getLastServerPosition().getZ()-realZ) > 70)

                if ((Math.abs(dz) > 200) && (Math.abs(dz) < 1500) && (Math.abs(_z - activeChar.getClientZ()) < 800)) {
                    activeChar.setXYZ(realX, realY, _z);
                    realZ = _z;
                } else {
                    if (Config.DEVELOPER) {
                        LOGGER.info(activeChar.getName() + ": Synchronizing position Server --> Client");
                    }

                    activeChar.sendPacket(new ValidateLocation(activeChar));
                }
            }
        }

        activeChar.setClientX(_x);
        activeChar.setClientY(_y);
        activeChar.setClientZ(_z);
        activeChar.setClientHeading(_heading); // No real need to validate heading.

        // Mobius: Check for possible door logout and move over exploit. Also checked at MoveBackwardToLocation.
        if (!DoorData.getInstance().checkIfDoorsBetween(realX, realY, realZ, _x, _y, _z, activeChar.getInstanceWorld(), false)) {
            activeChar.setLastServerPosition(realX, realY, realZ);
        }
    }
}
