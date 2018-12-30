package org.l2j.gameserver.templates.npc;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import io.github.joealisson.primitive.sets.IntSet;
import org.l2j.commons.geometry.Circle;
import org.l2j.gameserver.model.Territory;
import org.l2j.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public final class FakePlayerPath
{
	public static class Point
	{
		private final int _id;
		private final Territory _territory = new Territory();
		private final int _nextPointId;
		private final int _minDelay;
		private final int _maxDelay;
		private final boolean _sitting;

		public Point(int id, Location loc, int nextPointId, int minDelay, int maxDealy, boolean sitting, int offset)
		{
			_id = id;
			_territory.add(new Circle(loc.getX(), loc.getY(), offset).setZmin(loc.getZ() - 50).setZmax(loc.getZ() + 50));
			_nextPointId = nextPointId;
			_minDelay = minDelay;
			_maxDelay = maxDealy;
			_sitting = sitting;
		}

		public int getId()
		{
			return _id;
		}

		public Territory getTerritory()
		{
			return _territory;
		}

		public int getNextPointId()
		{
			return _nextPointId;
		}

		public int getMinDelay()
		{
			return _minDelay;
		}

		public int getMaxDelay()
		{
			return _maxDelay;
		}

		public boolean sitting()
		{
			return _sitting;
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(FakePlayerPath.class);

	private final int _id;
	private final Location _loc;
	private final IntSet _availNextPathes;
	private final IntObjectMap<Point> _points = new HashIntObjectMap<Point>();

	public FakePlayerPath(int id, Location loc, IntSet availNextPathes)
	{
		_id = id;
		_loc = loc;
		_availNextPathes = availNextPathes;
	}

	public int getId()
	{
		return _id;
	}

	public Location getLocation()
	{
		return _loc;
	}

	public boolean isAvailNextPath(int id)
	{
		return id != _id && _availNextPathes.contains(id);
	}

	public int[] getAvailNextPathes()
	{
		return _availNextPathes.toArray();
	}

	public Point getPoint(int id)
	{
		return _points.get(id);
	}

	public Point[] getPoints()
	{
		return _points.values().toArray(new Point[_points.size()]);
	}

	public void addPoint(Point point)
	{
		_points.put(point.getId(), point);
	}
}