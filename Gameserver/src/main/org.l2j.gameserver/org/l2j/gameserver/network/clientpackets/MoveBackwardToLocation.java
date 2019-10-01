package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.impl.DoorData;
import org.l2j.gameserver.enums.AdminTeleportType;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMoveRequest;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.world.zone.type.CastleZone;
import org.l2j.gameserver.world.zone.type.FortZone;
import org.l2j.gameserver.world.zone.type.HqZone;
import org.l2j.gameserver.world.zone.type.WaterZone;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.FlyToLocation.FlyType;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.MathUtil;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

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
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if ((Config.PLAYER_MOVEMENT_BLOCK_TIME > 0) && !activeChar.isGM() && (activeChar.getNotMoveUntil() > System.currentTimeMillis())) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC_ONE_MOMENT_PLEASE);
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if ((_targetX == _originX) && (_targetY == _originY) && (_targetZ == _originZ)) {
            activeChar.sendPacket(new StopMove(activeChar));
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Mobius: Check for possible door logout and move over exploit. Also checked at ValidatePosition.
        if (DoorData.getInstance().checkIfDoorsBetween(activeChar.getX(), activeChar.getY(), activeChar.getZ(), _targetX, _targetY, _targetZ, activeChar.getInstanceWorld(), false)) {
            activeChar.stopMove(activeChar.getLastServerPosition());
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Prevent player force moving in or out siege area.
        final AdminTeleportType teleMode = activeChar.getTeleMode();
        if (!activeChar.isFlying() && (teleMode == AdminTeleportType.NORMAL))
        {
            final boolean siegable = activeChar.isInsideZone(ZoneType.CASTLE) || activeChar.isInsideZone(ZoneType.FORT);
            boolean waterContact = activeChar.isInsideZone(ZoneType.WATER);
            if (siegable && !waterContact) // Need to know if activeChar is over water only when siegable.
            {
                for (Zone zone : ZoneManager.getInstance().getZones(_originX, _originY))
                {
                    if ((zone instanceof WaterZone) && ((zone.getArea().getHighZ() + activeChar.getCollisionHeight()) > _originZ))
                    {
                        waterContact = true;
                        break;
                    }
                }
            }
            if (activeChar.isInsideZone(ZoneType.HQ) || (siegable && waterContact))
            {
                boolean limited = false;
                boolean water = false;
                for (Zone zone : ZoneManager.getInstance().getZones(_targetX, _targetY, _targetZ))
                {
                    if ((zone instanceof CastleZone) || (zone instanceof FortZone))
                    {
                        if (!isInsideRadius3D(_originX, _originY, _originZ, _targetX, _targetY, _targetZ, 1000))
                        {
                            activeChar.stopMove(activeChar.getLastServerPosition());
                            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                            return;
                        }
                        limited = true;
                    }
                    if (zone instanceof WaterZone)
                    {
                        water = true;
                    }
                }
                if (limited && !water && !GeoEngine.getInstance().canSeeTarget(activeChar, new Location(_targetX, _targetY, _targetZ)))
                {
                    activeChar.stopMove(activeChar.getLastServerPosition());
                    activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
            else if (siegable)
            {
                for (Zone zone : ZoneManager.getInstance().getZones(_targetX, _targetY, _targetZ))
                {
                    if ((zone instanceof WaterZone) || (zone instanceof HqZone))
                    {
                        if ((Math.abs(_targetZ - _originZ) > 250) || !GeoEngine.getInstance().canSeeTarget(activeChar, new Location(_targetX, _targetY, _targetZ)))
                        {
                            activeChar.stopMove(activeChar.getLastServerPosition());
                            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                            return;
                        }
                    }
                    else if ((zone instanceof CastleZone) || (zone instanceof FortZone))
                    {
                        if (((Math.abs(_targetZ - _originZ) < 100) || (!MathUtil.isInsideRadius3D(_originX, _originY, _originZ, _targetX, _targetY, _targetZ, 2000))) && !GeoEngine.getInstance().canMoveToTarget(_originX, _originY, _originZ, _targetX, _targetY, _targetZ, activeChar.getInstanceWorld()))
                        {
                            activeChar.stopMove(activeChar.getLastServerPosition());
                            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                            return;
                        }
                    }
                }
            }
        }

        // Correcting targetZ from floor level to head level (?)
        // Client is giving floor level as targetZ but that floor level doesn't
        // match our current geodata and teleport coords as good as head level!
        // L2J uses floor, not head level as char coordinates. This is some
        // sort of incompatibility fix.
        // Validate position packets sends head level.
        _targetZ += activeChar.getTemplate().getCollisionHeight();

        if (!activeChar.isCursorKeyMovementActive() && (activeChar.isInFrontOf(new Location(_targetX, _targetY, _targetZ)) || activeChar.isOnSideOf(new Location(_originX, _originY, _originZ)))) {
            activeChar.setCursorKeyMovementActive(true);
        }

        if (_movementMode == 1) {
            activeChar.setCursorKeyMovement(false);
            final TerminateReturn terminate = EventDispatcher.getInstance().notifyEvent(new OnPlayerMoveRequest(activeChar, new Location(_targetX, _targetY, _targetZ)), activeChar, TerminateReturn.class);
            if ((terminate != null) && terminate.terminate()) {
                activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        } else // 0
        {
            if (!Config.ENABLE_KEYBOARD_MOVEMENT) {
                return;
            }
            activeChar.setCursorKeyMovement(true);
            if (!activeChar.isCursorKeyMovementActive()) {
                return;
            }
        }

        switch (teleMode) {
            case DEMONIC -> {
                activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                activeChar.teleToLocation(new Location(_targetX, _targetY, _targetZ));
                activeChar.setTeleMode(AdminTeleportType.NORMAL);
            }
            case CHARGE -> {
                activeChar.setXYZ(_targetX, _targetY, _targetZ);
                Broadcast.toSelfAndKnownPlayers(activeChar, new MagicSkillUse(activeChar, 30012, 10, 500, 0));
                Broadcast.toSelfAndKnownPlayers(activeChar, new FlyToLocation(activeChar, _targetX, _targetY, _targetZ, FlyType.CHARGE));
                Broadcast.toSelfAndKnownPlayers(activeChar, new MagicSkillLaunched(activeChar, 30012, 10));
                activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            }
            default -> {
                final double dx = _targetX - activeChar.getX();
                final double dy = _targetY - activeChar.getY();
                // Can't move if character is confused, or trying to move a huge distance
                if (activeChar.isControlBlocked() || (((dx * dx) + (dy * dy)) > 98010000)) // 9900*9900
                {
                    activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
                activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(_targetX, _targetY, _targetZ));
            }
        }

        // Mobius: Check spawn protections.
        if (activeChar.isSpawnProtected() || activeChar.isTeleportProtected()) {
            activeChar.onActionRequest();
        }
    }
}
