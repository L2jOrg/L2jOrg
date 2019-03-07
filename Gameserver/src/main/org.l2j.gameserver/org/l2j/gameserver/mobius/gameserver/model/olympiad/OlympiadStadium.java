/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.olympiad;

import com.l2jmobius.commons.util.Rnd;
import com.l2jmobius.gameserver.instancemanager.InstanceManager;
import com.l2jmobius.gameserver.model.L2Spawn;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.instancezone.Instance;
import com.l2jmobius.gameserver.model.zone.ZoneId;
import com.l2jmobius.gameserver.model.zone.type.L2OlympiadStadiumZone;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.ExOlympiadMatchEnd;
import com.l2jmobius.gameserver.network.serverpackets.ExOlympiadUserInfo;
import com.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author JIV
 */
public class OlympiadStadium
{
	private static final Logger LOGGER = Logger.getLogger(OlympiadStadium.class.getName());
	private final L2OlympiadStadiumZone _zone;
	private final Instance _instance;
	private final List<L2Spawn> _buffers;
	private OlympiadGameTask _task = null;
	
	protected OlympiadStadium(L2OlympiadStadiumZone olyzone, int stadium)
	{
		_zone = olyzone;
		_instance = InstanceManager.getInstance().createInstance(olyzone.getInstanceTemplateId(), null);
		_buffers = _instance.getNpcs().stream().map(L2Npc::getSpawn).collect(Collectors.toList());
		_buffers.stream().map(L2Spawn::getLastSpawn).forEach(L2Npc::decayMe);
	}
	
	public L2OlympiadStadiumZone getZone()
	{
		return _zone;
	}
	
	public final void registerTask(OlympiadGameTask task)
	{
		_task = task;
	}
	
	public OlympiadGameTask getTask()
	{
		return _task;
	}
	
	public Instance getInstance()
	{
		return _instance;
	}
	
	public final void openDoors()
	{
		_instance.getDoors().forEach(L2DoorInstance::openMe);
	}
	
	public final void closeDoors()
	{
		_instance.getDoors().forEach(L2DoorInstance::closeMe);
	}
	
	public final void spawnBuffers()
	{
		_buffers.forEach(L2Spawn::startRespawn);
		_buffers.forEach(L2Spawn::doSpawn);
	}
	
	public final void deleteBuffers()
	{
		_buffers.forEach(L2Spawn::stopRespawn);
		_buffers.stream().map(L2Spawn::getLastSpawn).filter(Objects::nonNull).forEach(L2Npc::deleteMe);
	}
	
	public final void broadcastStatusUpdate(L2PcInstance player)
	{
		final ExOlympiadUserInfo packet = new ExOlympiadUserInfo(player);
		for (L2PcInstance target : _instance.getPlayers())
		{
			if (target.inObserverMode() || (target.getOlympiadSide() != player.getOlympiadSide()))
			{
				target.sendPacket(packet);
			}
		}
	}
	
	public final void broadcastPacket(IClientOutgoingPacket packet)
	{
		_instance.broadcastPacket(packet);
	}
	
	public final void broadcastPacketToObservers(IClientOutgoingPacket packet)
	{
		for (L2PcInstance target : _instance.getPlayers())
		{
			if (target.inObserverMode())
			{
				target.sendPacket(packet);
			}
		}
	}
	
	public final void updateZoneStatusForCharactersInside()
	{
		if (_task == null)
		{
			return;
		}
		
		final boolean battleStarted = _task.isBattleStarted();
		final SystemMessage sm;
		if (battleStarted)
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
		}
		else
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
		}
		
		for (L2PcInstance player : _instance.getPlayers())
		{
			if (player.inObserverMode())
			{
				return;
			}
			
			if (battleStarted)
			{
				player.setInsideZone(ZoneId.PVP, true);
				player.sendPacket(sm);
			}
			else
			{
				player.setInsideZone(ZoneId.PVP, false);
				player.sendPacket(sm);
				player.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
			}
		}
	}
	
	public final void updateZoneInfoForObservers()
	{
		if (_task == null)
		{
			return;
		}
		
		for (L2PcInstance player : _instance.getPlayers())
		{
			if (!player.inObserverMode())
			{
				return;
			}
			
			final OlympiadGameTask nextArena = OlympiadGameManager.getInstance().getOlympiadTask(player.getOlympiadGameId());
			final List<Location> spectatorSpawns = nextArena.getStadium().getZone().getSpectatorSpawns();
			if (spectatorSpawns.isEmpty())
			{
				LOGGER.warning(getClass().getSimpleName() + ": Zone: " + nextArena.getStadium().getZone() + " doesn't have specatator spawns defined!");
				return;
			}
			final Location loc = spectatorSpawns.get(Rnd.get(spectatorSpawns.size()));
			player.enterOlympiadObserverMode(loc, player.getOlympiadGameId());
		}
	}
}