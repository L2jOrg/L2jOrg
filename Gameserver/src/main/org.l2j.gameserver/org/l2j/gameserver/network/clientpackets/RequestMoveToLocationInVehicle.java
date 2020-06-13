/*
 * Copyright © 2019-2020 L2JOrg
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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.BoatManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Boat;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.MoveToLocationInVehicle;
import org.l2j.gameserver.network.serverpackets.StopMoveInVehicle;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

public final class RequestMoveToLocationInVehicle extends ClientPacket {
    private int _boatId;
    private int _targetX;
    private int _targetY;
    private int _targetZ;
    private int _originX;
    private int _originY;
    private int _originZ;

    @Override
    public void readImpl() {
        _boatId = readInt(); // objectId of boat
        _targetX = readInt();
        _targetY = readInt();
        _targetZ = readInt();
        _originX = readInt();
        _originY = readInt();
        _originZ = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if ((Config.PLAYER_MOVEMENT_BLOCK_TIME > 0) && !activeChar.isGM() && (activeChar.getNotMoveUntil() > System.currentTimeMillis())) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC_ONE_MOMENT_PLEASE);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if ((_targetX == _originX) && (_targetY == _originY) && (_targetZ == _originZ)) {
            client.sendPacket(new StopMoveInVehicle(activeChar, _boatId));
            return;
        }

        if (activeChar.isAttackingNow() && (activeChar.getActiveWeaponItem() != null) && (activeChar.getActiveWeaponItem().getItemType() == WeaponType.BOW)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (activeChar.isSitting() || activeChar.isMovementDisabled()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (activeChar.hasSummon()) {
            client.sendPacket(SystemMessageId.YOU_SHOULD_RELEASE_YOUR_SERVITOR_SO_THAT_IT_DOES_NOT_FALL_OFF_OF_THE_BOAT_AND_DROWN);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (activeChar.isTransformed()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final Boat boat;
        if (activeChar.isInBoat()) {
            boat = activeChar.getBoat();
            if (boat.getObjectId() != _boatId) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        } else {
            boat = BoatManager.getInstance().getBoat(_boatId);
            if ((boat == null) || !isInsideRadius3D(boat, activeChar, 300)) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            activeChar.setVehicle(boat);
        }

        final Location pos = new Location(_targetX, _targetY, _targetZ);
        final Location originPos = new Location(_originX, _originY, _originZ);
        activeChar.setInVehiclePosition(pos);
        activeChar.broadcastPacket(new MoveToLocationInVehicle(activeChar, pos, originPos));
    }
}
