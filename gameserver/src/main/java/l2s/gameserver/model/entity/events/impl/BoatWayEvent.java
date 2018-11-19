package l2s.gameserver.model.entity.events.impl;

import java.util.List;

import l2s.commons.collections.LazyArrayList;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.data.BoatHolder;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.boat.Boat;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.objects.BoatPoint;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.MapUtils;

/**
 * @author VISTALL
 * @date  17:48/26.12.2010
 */
public class BoatWayEvent extends Event
{
	public static final String BOAT_POINTS = "boat_points";

	private final int _ticketId;
	private final Location _returnLoc;
	private final Boat _boat;
	private final Location[] _broadcastPoints;

	public BoatWayEvent(MultiValueSet<String> set)
	{
		super(set);
		_ticketId = set.getInteger("ticketId", 0);
		_returnLoc = Location.parseLoc(set.getString("return_point"));
		String className = set.getString("class", null);
		if(className != null)
		{
			_boat = BoatHolder.getInstance().initBoat(getName(), className);
			Location loc = Location.parseLoc(set.getString("spawn_point"));
			_boat.setLoc(loc, true);
			_boat.setHeading(loc.h);
		}
		else
		{
			_boat = BoatHolder.getInstance().getBoat(getName());
		}
		_boat.setWay(className != null ? 1 : 0, this);

		final String brPoints = set.getString("broadcast_point", null);
		if(brPoints == null)
		{
			_broadcastPoints = new Location[1];
			_broadcastPoints[0] = _boat.getLoc();
		}
		else
		{
			final String[] points = brPoints.split(";");
			_broadcastPoints = new Location[points.length];
			for(int i = 0; i < points.length; i++)
				_broadcastPoints[i] = Location.parseLoc(points[i]);
		}
	}

	@Override
	public void initEvent()
	{}

	@Override
	public void startEvent()
	{
		L2GameServerPacket startPacket = _boat.startPacket();
		for(Player player : _boat.getPlayers())
		{
			if(_ticketId > 0)
			{
				if(player.consumeItem(_ticketId, 1, true))
				{
					if(startPacket != null)
						player.sendPacket(startPacket);
				}
				else
				{
					player.sendPacket(SystemMsg.YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT);
					_boat.oustPlayer(player, _returnLoc, true);
				}
			}
			else
			{
				if(startPacket != null)
					player.sendPacket(startPacket);
			}
		}

		moveNext();
	}

	public void moveNext()
	{
		List<BoatPoint> points = getObjects(BOAT_POINTS);

		if(_boat.getRunState() >= points.size())
		{
			_boat.trajetEnded(true);
			clearActions();
			return;
		}

		final BoatPoint bp = points.get(_boat.getRunState());

		if(bp.getSpeed1() >= 0)
			_boat.setMoveSpeed(bp.getSpeed1());
		if(bp.getSpeed2() >= 0)
			_boat.setRotationSpeed(bp.getSpeed2());

		if(_boat.getRunState() == 0)
			_boat.broadcastCharInfo();

		_boat.setRunState(_boat.getRunState() + 1);

		if(bp.isTeleport())
			_boat.teleportShip(bp.getX(), bp.getY(), bp.getZ());
		else
			_boat.moveToLocation(bp.getX(), bp.getY(), bp.getZ(), 0, false);
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		registerActions();
	}

	@Override
	public EventType getType()
	{
		return EventType.BOAT_EVENT;
	}

	@Override
	protected long startTimeMillis()
	{
		return System.currentTimeMillis();
	}

	@Override
	public List<Player> broadcastPlayers(int range)
	{
		List<Player> players = new LazyArrayList<Player>(64);
		if(range > 0)
		{
			for(Location loc : _broadcastPoints)
			{
				for(Player player : GameObjectsStorage.getPlayers())
				{
					if(!player.getReflection().isMain())
						continue;

					if(player.isInRangeZ(loc, range) && !players.contains(player))
						players.add(player);
				}
			}
		}
		else
		{
			for(Location loc : _broadcastPoints)
			{
				int rx = MapUtils.regionX(loc.getX());
				int ry = MapUtils.regionY(loc.getY());
				for(Player player : GameObjectsStorage.getPlayers())
				{
					if(!player.getReflection().isMain())
						continue;

					int tx = MapUtils.regionX(player) - rx;
					int ty = MapUtils.regionY(player) - ry;
					if(tx * tx + ty * ty <= Config.SHOUT_SQUARE_OFFSET && !players.contains(player))
						players.add(player);
				}

			}
		} return players;
	}

	@Override
	public void printInfo()
	{
		//
	}

	public Location getReturnLoc()
	{
		return _returnLoc;
	}
}