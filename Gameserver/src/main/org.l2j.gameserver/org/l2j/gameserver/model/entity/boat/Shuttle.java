package org.l2j.gameserver.model.entity.boat;

import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.impl.ShuttleWayEvent;
import org.l2j.gameserver.network.l2.s2c.*;
import org.l2j.gameserver.templates.ShuttleTemplate;
import org.l2j.gameserver.utils.Location;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
 */
public class Shuttle extends Boat
{
	private static class Docked extends RunnableImpl
	{
		private Shuttle _shuttle;

		public Docked(Shuttle shuttle)
		{
			_shuttle = shuttle;
		}

		public void runImpl()
		{
			if(_shuttle == null)
				return;

			_shuttle.getCurrentFloor().stopEvent(false);
			_shuttle.getNextFloor().reCalcNextTime(false);
		}
	}

	private static final long serialVersionUID = 1L;

	private final HashIntObjectMap<ShuttleWayEvent> _floors = new HashIntObjectMap<ShuttleWayEvent>();

	private boolean _moveBack;
	public int _currentWay;

	public Shuttle(int objectId, ShuttleTemplate template)
	{
		super(objectId, template);
		setFlying(true);
	}

	@Override
	public int getBoatId()
	{
		return getTemplate().getId();
	}

	@Override
	public final ShuttleTemplate getTemplate()
	{
		return (ShuttleTemplate) super.getTemplate();
	}

	@Override
	public void onSpawn()
	{
		_moveBack = false;
		_currentWay = 0;

		getCurrentFloor().reCalcNextTime(false);
	}

	@Override
	public void onEvtArrived()
	{
		ThreadPoolManager.getInstance().schedule(new Docked(this), 1500L);
	}

	@Override
	public L2GameServerPacket infoPacket()
	{
		return new ExShuttleInfoPacket(this);
	}

	@Override
	public L2GameServerPacket movePacket()
	{
		return new ExSuttleMovePacket(this);
	}

	@Override
	public L2GameServerPacket inMovePacket(Player player, Location src, Location desc)
	{
		return new ExMTLInSuttlePacket(player, this, src, desc);
	}

	@Override
	public L2GameServerPacket stopMovePacket()
	{
		return null;
	}

	@Override
	public L2GameServerPacket inStopMovePacket(Player player)
	{
		return new ExStopMoveInShuttlePacket(player);
	}

	@Override
	public L2GameServerPacket startPacket()
	{
		return null;
	}

	@Override
	public L2GameServerPacket checkLocationPacket()
	{
		return null;
	}

	@Override
	public L2GameServerPacket validateLocationPacket(Player player)
	{
		return new ExValidateLocationInShuttlePacket(player);
	}

	@Override
	public L2GameServerPacket getOnPacket(Playable playable, Location location)
	{
		return new ExSuttleGetOnPacket(playable, this, location);
	}

	@Override
	public L2GameServerPacket getOffPacket(Playable playable, Location location)
	{
		return new ExSuttleGetOffPacket(playable, this, location);
	}

	@Override
	public boolean isShuttle()
	{
		return true;
	}

	@Override
	public void oustPlayers()
	{
		//
	}

	@Override
	public void trajetEnded(boolean oust)
	{
		//
	}

	@Override
	public void teleportShip(int x, int y, int z)
	{
		//
	}

	@Override
	public Location getReturnLoc()
	{
		return getCurrentFloor().getReturnLoc();
	}

	@Override
	public void addPlayer(Player player, Location boatLoc)
	{
		if(isMoving)
		{
			player.teleToLocation(getReturnLoc());
			return;
		}
		super.addPlayer(player, boatLoc);
	}

	@Override
	public void oustPlayer(Player player, Location loc, boolean teleport)
	{
		if(isMoving)
		{
			player.teleToLocation(getReturnLoc());
			return;
		}
		super.oustPlayer(player, loc, teleport);
	}

	public void addFloor(ShuttleWayEvent floor)
	{
		_floors.put((floor.getId() % 100), floor);
	}

	public ShuttleWayEvent getCurrentFloor()
	{
		return _floors.get(_currentWay);
	}

	private ShuttleWayEvent getNextFloor()
	{
		int floors = _floors.size() - 1;
		if(!_moveBack)
		{
			_currentWay++;
			if(_currentWay > floors)
			{
				_currentWay = floors - 1;
				_moveBack = true;
			}
		}
		else
		{
			_currentWay--;
			if(_currentWay < 0)
			{
				_currentWay = 1;
				_moveBack = false;
			}
		}
		return _floors.get(_currentWay);
	}
}
