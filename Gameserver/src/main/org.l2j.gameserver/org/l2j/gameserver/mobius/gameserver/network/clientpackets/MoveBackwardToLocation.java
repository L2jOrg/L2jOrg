package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.DoorData;
import org.l2j.gameserver.mobius.gameserver.enums.AdminTeleportType;
import org.l2j.gameserver.mobius.gameserver.enums.SayuneType;
import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.SayuneEntry;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player.OnPlayerMoveRequest;
import org.l2j.gameserver.mobius.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.*;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.FlyToLocation.FlyType;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.sayune.ExFlyMove;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.sayune.ExFlyMoveBroadcast;
import org.l2j.gameserver.mobius.gameserver.util.Broadcast;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MoveBackwardToLocation extends IClientIncomingPacket
{
	private int _targetX;
	private int _targetY;
	private int _targetZ;
	private int _originX;
	private int _originY;
	private int _originZ;
	private int _movementMode;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_targetX = packet.getInt();
		_targetY = packet.getInt();
		_targetZ = packet.getInt();
		_originX = packet.getInt();
		_originY = packet.getInt();
		_originZ = packet.getInt();
		_movementMode = packet.getInt(); // is 0 if cursor keys are used 1 if mouse is used
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if ((Config.PLAYER_MOVEMENT_BLOCK_TIME > 0) && !activeChar.isGM() && (activeChar.getNotMoveUntil() > System.currentTimeMillis()))
		{
			activeChar.sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC_ONE_MOMENT_PLEASE);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((_targetX == _originX) && (_targetY == _originY) && (_targetZ == _originZ))
		{
			activeChar.sendPacket(new StopMove(activeChar));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Mobius: Check for possible door logout and move over exploit. Also checked at ValidatePosition.
		if (DoorData.getInstance().checkIfDoorsBetween(activeChar.getX(), activeChar.getY(), activeChar.getZ(), _targetX, _targetY, _targetZ, activeChar.getInstanceWorld(), false))
		{
			activeChar.stopMove(activeChar.getLastServerPosition());
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Correcting targetZ from floor level to head level (?)
		// Client is giving floor level as targetZ but that floor level doesn't
		// match our current geodata and teleport coords as good as head level!
		// L2J uses floor, not head level as char coordinates. This is some
		// sort of incompatibility fix.
		// Validate position packets sends head level.
		_targetZ += activeChar.getTemplate().getCollisionHeight();
		
		if (!activeChar.isCursorKeyMovementActive() && (activeChar.isInFrontOf(new Location(_targetX, _targetY, _targetZ)) || activeChar.isOnSideOf(new Location(_originX, _originY, _originZ))))
		{
			activeChar.setCursorKeyMovementActive(true);
		}
		
		if (_movementMode == 1)
		{
			activeChar.setCursorKeyMovement(false);
			final TerminateReturn terminate = EventDispatcher.getInstance().notifyEvent(new OnPlayerMoveRequest(activeChar, new Location(_targetX, _targetY, _targetZ)), activeChar, TerminateReturn.class);
			if ((terminate != null) && terminate.terminate())
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		else // 0
		{
			if (!Config.ENABLE_KEYBOARD_MOVEMENT)
			{
				return;
			}
			activeChar.setCursorKeyMovement(true);
			if (!activeChar.isCursorKeyMovementActive())
			{
				return;
			}
		}
		
		switch (activeChar.getTeleMode())
		{
			case DEMONIC:
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				activeChar.teleToLocation(new Location(_targetX, _targetY, _targetZ));
				activeChar.setTeleMode(AdminTeleportType.NORMAL);
				break;
			}
			case SAYUNE:
			{
				activeChar.sendPacket(new ExFlyMove(activeChar, SayuneType.ONE_WAY_LOC, -1, Arrays.asList(new SayuneEntry(false, -1, _targetX, _targetY, _targetZ))));
				activeChar.setXYZ(_targetX, _targetY, _targetZ);
				Broadcast.toKnownPlayers(activeChar, new ExFlyMoveBroadcast(activeChar, SayuneType.ONE_WAY_LOC, -1, new Location(_targetX, _targetY, _targetZ)));
				activeChar.setTeleMode(AdminTeleportType.NORMAL);
				break;
			}
			case CHARGE:
			{
				activeChar.setXYZ(_targetX, _targetY, _targetZ);
				Broadcast.toSelfAndKnownPlayers(activeChar, new MagicSkillUse(activeChar, 30012, 10, 500, 0));
				Broadcast.toSelfAndKnownPlayers(activeChar, new FlyToLocation(activeChar, _targetX, _targetY, _targetZ, FlyType.CHARGE));
				Broadcast.toSelfAndKnownPlayers(activeChar, new MagicSkillLaunched(activeChar, 30012, 10));
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				break;
			}
			default:
			{
				final double dx = _targetX - activeChar.getX();
				final double dy = _targetY - activeChar.getY();
				// Can't move if character is confused, or trying to move a huge distance
				if (activeChar.isControlBlocked() || (((dx * dx) + (dy * dy)) > 98010000)) // 9900*9900
				{
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(_targetX, _targetY, _targetZ));
				break;
			}
		}
		
		// Mobius: Check spawn protections.
		if (activeChar.isSpawnProtected() || activeChar.isTeleportProtected())
		{
			activeChar.onActionRequest();
		}
	}
}
