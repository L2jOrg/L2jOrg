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
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.enums.AdminTeleportType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMoveRequest;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.FlyToLocation.FlyType;
import org.l2j.gameserver.util.Broadcast;

public class MoveBackwardToLocation extends ClientPacket {
    private int _targetX;
    private int _targetY;
    private int _targetZ;
    private int _originX;
    private int _originY;
    private int _originZ;
    private int _movementMode;

    @Override
    public void readImpl() {
        _targetX = readInt();
        _targetY = readInt();
        _targetZ = readInt();
        _originX = readInt();
        _originY = readInt();
        _originZ = readInt();
        _movementMode = readInt(); // is 0 if cursor keys are used 1 if mouse is used
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if ((Config.PLAYER_MOVEMENT_BLOCK_TIME > 0) && !player.isGM() && (player.getNotMoveUntil() > System.currentTimeMillis())) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC_ONE_MOMENT_PLEASE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if ((_targetX == _originX) && (_targetY == _originY) && (_targetZ == _originZ)) {
            player.sendPacket(new StopMove(player));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Mobius: Check for possible door logout and move over exploit. Also checked at ValidatePosition.
        if (DoorDataManager.getInstance().checkIfDoorsBetween(player.getX(), player.getY(), player.getZ(), _targetX, _targetY, _targetZ, player.getInstanceWorld(), false)) {
            player.stopMove(player.getLastServerPosition());
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Correcting targetZ from floor level to head level (?)
        // Client is giving floor level as targetZ but that floor level doesn't
        // match our current geodata and teleport coords as good as head level!
        // L2J uses floor, not head level as char coordinates. This is some
        // sort of incompatibility fix.
        // Validate position packets sends head level.
        _targetZ += player.getTemplate().getCollisionHeight();

        if (!player.isCursorKeyMovementActive() && (player.isInFrontOf(new Location(_targetX, _targetY, _targetZ)) || player.isOnSideOf(new Location(_originX, _originY, _originZ)))) {
            player.setCursorKeyMovementActive(true);
        }

        if (_movementMode == 1) {
            player.setCursorKeyMovement(false);
            final TerminateReturn terminate = EventDispatcher.getInstance().notifyEvent(new OnPlayerMoveRequest(player, new Location(_targetX, _targetY, _targetZ)), player, TerminateReturn.class);
            if ((terminate != null) && terminate.terminate()) {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        } else // 0
        {
            if (!Config.ENABLE_KEYBOARD_MOVEMENT) {
                return;
            }
            player.setCursorKeyMovement(true);
            if (!player.isCursorKeyMovementActive()) {
                return;
            }
        }

        final AdminTeleportType teleMode = player.getTeleMode();
        switch (teleMode) {
            case DEMONIC -> {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                player.teleToLocation(new Location(_targetX, _targetY, _targetZ));
                player.setTeleMode(AdminTeleportType.NORMAL);
            }
            case CHARGE -> {
                player.setXYZ(_targetX, _targetY, _targetZ);
                Broadcast.toSelfAndKnownPlayers(player, new MagicSkillUse(player, 30012, 10, 500, 0));
                Broadcast.toSelfAndKnownPlayers(player, new FlyToLocation(player, _targetX, _targetY, _targetZ, FlyType.CHARGE));
                Broadcast.toSelfAndKnownPlayers(player, new MagicSkillLaunched(player, 30012, 10));
                player.sendPacket(ActionFailed.STATIC_PACKET);
            }
            default -> {
                final double dx = _targetX - player.getX();
                final double dy = _targetY - player.getY();
                // Can't move if character is confused, or trying to move a huge distance
                if (player.isControlBlocked() || (((dx * dx) + (dy * dy)) > 98010000)) // 9900*9900
                {
                    player.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(_targetX, _targetY, _targetZ));
            }
        }

        player.onActionRequest();
    }
}
