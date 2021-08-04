/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.engine.geo.SyncMode;
import org.l2j.gameserver.engine.geo.settings.GeoEngineSettings;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;

public class ValidatePosition extends ClientPacket {

    private int _x;
    private int _y;
    private int _z;
    private int _heading;

    @Override
    public void readImpl() {
        _x = readInt();
        _y = readInt();
        _z = readInt();
        _heading = readInt();
        readInt(); // vehicle id
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null || player.isTeleporting() || player.isInObserverMode()) {
            return;
        }

        final int realX = player.getX();
        final int realY = player.getY();
        int realZ = player.getZ();

        if (!checkPositions(player, realX, realY, realZ)) {
            return;
        }

        int dx;
        int dy;
        int dz;
        double diffSq;

        dx = _x - realX;
        dy = _y - realY;
        dz = _z - realZ;
        diffSq = ((dx * dx) + (dy * dy));

        if(diffSq < 10) {
            return;
        }

        // Don't allow flying transformations outside gracia area!
        if (player.isFlyingMounted() && (_x > World.GRACIA_MAX_X)) {
            player.untransform();
        }

        if (player.isFlying() || player.isInsideZone(ZoneType.WATER)) {
            onMoveNoTerrainEnv(player, realX, realY, diffSq);
        } else if (diffSq < 360000) { // if too large, messes observation
            if (checkNonGeoEngineMovement(player, realX, realY, diffSq)) {
                return;
            }
            realZ = updateZPosition(player, realX, realY, realZ, dz, diffSq);
        }

        player.setClientZ(_z);

        // Mobius: Check for possible door logout and move over exploit. Also checked at MoveBackwardToLocation.
        if (!DoorDataManager.getInstance().checkIfDoorsBetween(realX, realY, realZ, _x, _y, _z, player.getInstanceWorld(), false)) {
            player.setLastServerPosition(realX, realY, realZ);
        }
    }

    private int updateZPosition(Player player, int realX, int realY, int realZ, int dz, double diffSq) {
        // Sync 2 (or other),
        // intended for geodata. Sends a validation packet to client
        // when too far from server calculated true coordinate.
        // Due to geodata/zone errors, some Z axis checks are made. (maybe a temporary solution)
        // Important: this code part must work together with Creature.updatePosition
        if ((diffSq > 250000) || (Math.abs(dz) > 200)) {
           if ((Math.abs(dz) > 200) && (Math.abs(dz) < 1500) && (Math.abs(_z - player.getClientZ()) < 800)) {
                player.setXYZ(realX, realY, _z);
                realZ = _z;
            } else {
                if(player.isFalling(_z)) {
                    player.setXYZ(realX, realY, _z);
                }
                player.sendPacket(new ValidateLocation(player));
            }
        }
        return realZ;
    }

    private boolean checkNonGeoEngineMovement(Player player, int realX, int realY, double diffSq) {
        if (GeoEngineSettings.isSyncMode(SyncMode.Z_ONLY)) {  // mainly used when no geodata but can be used also with geodata
            player.setXYZ(realX, realY, _z);
            return true;
        }
        if (GeoEngineSettings.isSyncMode(SyncMode.CLIENT)) { // Trusting also client x,y coordinates (should not be used with geodata)
            checkClientMovement(player, realX, realY, diffSq);
            return true;
        }
        return false;
    }

    private void onMoveNoTerrainEnv(Player player, int realX, int realY, double diffSq) {
        player.setXYZ(realX, realY, _z);
        if (diffSq > 90000) {
            player.sendPacket(new ValidateLocation(player));
        }
    }

    private void checkClientMovement(Player player, int realX, int realY, double diffSq) {
        if (!player.isMoving() || !player.validateMovementHeading(_heading)) { // Heading changed on client = possible obstacle
            // character is not moving, take coordinates from client
            if (diffSq < 2500) {
                player.setXYZ(realX, realY, _z);
            } else {
                player.setXYZ(_x, _y, _z);
            }
        } else {
            player.setXYZ(realX, realY, _z);
        }

        player.setHeading(_heading);
    }

    private boolean checkPositions(Player player, int realX, int realY, int realZ) {
        if(realX == _x && realY == _y && realZ == _z) {
            return false;
        }

        if (_x == 0 && _y == 0) {
            if (realX != 0) {
                return false;
            }
        }

        return !player.isFalling(_z);
    }
}
