package org.l2j.gameserver.utils;

import java.io.Serializable;

import org.dom4j.Element;
import org.l2j.commons.geometry.Point3D;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.templates.spawn.SpawnRange;

public class Location extends Point3D implements SpawnRange, Serializable
{
	private static final long serialVersionUID = 1L;

	public int h;

	public Location()
	{}

	/**
	 * Позиция (x, y, z, heading)
	 */
	public Location(int x, int y, int z, int heading)
	{
		super(x, y, z);
		h = heading;
	}

	public Location(int x, int y, int z)
	{
		this(x, y, z, 0);
	}

	public Location(int[] xyz)
	{
		this(xyz[0], xyz[1], xyz[2], 0);
	}

	public Location(GameObject obj)
	{
		this(obj.getX(), obj.getY(), obj.getZ(), obj.getHeading());
	}

	public Location changeZ(int zDiff)
	{
		z += zDiff;
		return this;
	}

	public Location correctGeoZ()
	{
		z = GeoEngine.getHeight(x, y, z, 0);
		return this;
	}

	public Location correctGeoZ(int geoIndex)
	{
		z = GeoEngine.getHeight(x, y, z, geoIndex);
		return this;
	}

	public Location setX(int x)
	{
		this.x = x;
		return this;
	}

	public Location setY(int y)
	{
		this.y = y;
		return this;
	}

	public Location setZ(int z)
	{
		this.z = z;
		return this;
	}

	public Location setH(int h)
	{
		this.h = h;
		return this;
	}

	public Location set(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Location set(int x, int y, int z, int h)
	{
		set(x, y, z);
		this.h = h;
		return this;
	}

	public Location set(Location loc)
	{
		x = loc.x;
		y = loc.y;
		z = loc.z;
		h = loc.h;
		return this;
	}

	public Location world2geo()
	{
		x = x - World.MAP_MIN_X >> 4;
		y = y - World.MAP_MIN_Y >> 4;
		return this;
	}

	public Location geo2world()
	{
		// размер одного блока 16*16 точек, +8*+8 это его средина
		x = (x << 4) + World.MAP_MIN_X + 8;
		y = (y << 4) + World.MAP_MIN_Y + 8;
		return this;
	}

	public double distance(Location loc)
	{
		return distance(loc.x, loc.y);
	}

	public double distance(int x, int y)
	{
		long dx = this.x - x;
		long dy = this.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double distance3D(Location loc)
	{
		return distance3D(loc.x, loc.y, loc.z);
	}

	public double distance3D(int x, int y, int z)
	{
		long dx = this.x - x;
		long dy = this.y - y;
		long dz = this.z - z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	@Override
	public Location clone()
	{
		return new Location(x, y, z, h);
	}

	@Override
	public final String toString()
	{
		return x + "," + y + "," + z + "," + h;
	}

	public boolean isNull()
	{
		return x == 0 || y == 0 || z == 0;
	}

	public final String toXYZString()
	{
		return x + " " + y + " " + z;
	}

	/**
	 * Парсит Location из строки, где координаты разделены пробелами или запятыми
	 */
	public static Location parseLoc(String s) throws IllegalArgumentException
	{
		String[] xyzh = s.split("[\\s,;]+");
		if(xyzh.length < 3)
			throw new IllegalArgumentException("Can't parse location from string: " + s);
		int x = Integer.parseInt(xyzh[0]);
		int y = Integer.parseInt(xyzh[1]);
		int z = Integer.parseInt(xyzh[2]);
		int h = xyzh.length < 4 ? 0 : Integer.parseInt(xyzh[3]);
		return new Location(x, y, z, h);
	}

	public static Location parse(Element element)
	{
		int x = Integer.parseInt(element.attributeValue("x"));
		int y = Integer.parseInt(element.attributeValue("y"));
		int z = Integer.parseInt(element.attributeValue("z"));
		int h = element.attributeValue("h") == null ? 0 : Integer.parseInt(element.attributeValue("h"));
		return new Location(x, y, z, h);
	}

	/**
	 * Найти стабильную точку перед объектом obj1 для спавна объекта obj2, с учетом heading
	 *
	 * @param obj
	 * @param obj2
	 * @param radiusmin
	 * @param radiusmax
	 * @return
	 */
	public static Location findFrontPosition(GameObject obj, GameObject obj2, int radiusmin, int radiusmax)
	{
		if(radiusmax == 0 || radiusmax < radiusmin)
			return new Location(obj);

		double collision = obj.getCollisionRadius() + obj2.getCollisionRadius();
		int randomRadius, randomAngle, tempz;
		int minangle = 0;
		int maxangle = 360;

		if(!obj.equals(obj2))
		{
			double angle = PositionUtils.calculateAngleFrom(obj, obj2);
			minangle = (int) angle - 45;
			maxangle = (int) angle + 45;
		}

		Location pos = new Location();
		for(int i = 0; i < 100; i++)
		{
			randomRadius = Rnd.get(radiusmin, radiusmax);
			randomAngle = Rnd.get(minangle, maxangle);
			pos.x = obj.getX() + (int) ((collision + randomRadius) * Math.cos(Math.toRadians(randomAngle)));
			pos.y = obj.getY() + (int) ((collision + randomRadius) * Math.sin(Math.toRadians(randomAngle)));
			pos.z = obj.getZ();
			tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, obj.getGeoIndex());
			if(Math.abs(pos.z - tempz) < 200 && GeoEngine.getNSWE(pos.x, pos.y, tempz, obj.getGeoIndex()) == GeoEngine.NSWE_ALL)
			{
				pos.z = tempz;
				if(!obj.equals(obj2))
					pos.h = PositionUtils.getHeadingTo(pos, obj2.getLoc());
				else
					pos.h = obj.getHeading();
				return pos;
			}
		}

		return new Location(obj);
	}

	/**
	 * Найти точку в пределах досягаемости от начальной
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param radiusmin
	 * @param radiusmax
	 * @param geoIndex
	 * @return
	 */
	public static Location findAroundPosition(int x, int y, int z, int radiusmin, int radiusmax, int geoIndex)
	{
		Location pos;
		int tempz;
		for(int i = 0; i < 100; i++)
		{
			pos = Location.coordsRandomize(x, y, z, 0, radiusmin, radiusmax);
			tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, geoIndex);
			if(GeoEngine.canMoveToCoord(x, y, z, pos.x, pos.y, tempz, geoIndex) && GeoEngine.canMoveToCoord(pos.x, pos.y, tempz, x, y, z, geoIndex))
			{
				pos.z = tempz;
				return pos;
			}
		}
		return new Location(x, y, z);
	}

	public static Location findAroundPosition(Location loc, int radius, int geoIndex)
	{
		return findAroundPosition(loc.x, loc.y, loc.z, 0, radius, geoIndex);
	}

	public static Location findAroundPosition(Location loc, int radiusmin, int radiusmax, int geoIndex)
	{
		return findAroundPosition(loc.x, loc.y, loc.z, radiusmin, radiusmax, geoIndex);
	}

	public static Location findAroundPosition(GameObject obj, Location loc, int radiusmin, int radiusmax)
	{
		return findAroundPosition(loc.x, loc.y, loc.z, radiusmin, radiusmax, obj.getGeoIndex());
	}

	public static Location findAroundPosition(GameObject obj, int radiusmin, int radiusmax)
	{
		return findAroundPosition(obj, obj.getLoc(), radiusmin, radiusmax);
	}

	public static Location findAroundPosition(GameObject obj, int radius)
	{
		return findAroundPosition(obj, 0, radius);
	}

	/**
	 * Найти стабильную точку в пределах радиуса от начальной
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param radiusmin
	 * @param radiusmax
	 * @param geoIndex
	 * @return
	 */
	public static Location findPointToStay(int x, int y, int z, int radiusmin, int radiusmax, int geoIndex)
	{
		Location pos;
		int tempz;
		for(int i = 0; i < 100; i++)
		{
			pos = Location.coordsRandomize(x, y, z, 0, radiusmin, radiusmax);
			tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, geoIndex);
			if(Math.abs(pos.z - tempz) < 200 && GeoEngine.getNSWE(pos.x, pos.y, tempz, geoIndex) == GeoEngine.NSWE_ALL)
			{
				pos.z = tempz;
				return pos;
			}
		}
		return new Location(x, y, z);
	}

	public static Location findPointToStay(Location loc, int radius, int geoIndex)
	{
		return findPointToStay(loc.x, loc.y, loc.z, 0, radius, geoIndex);
	}

	public static Location findPointToStay(Location loc, int radiusmin, int radiusmax, int geoIndex)
	{
		return findPointToStay(loc.x, loc.y, loc.z, radiusmin, radiusmax, geoIndex);
	}

	public static Location findPointToStay(GameObject obj, Location loc, int radiusmin, int radiusmax)
	{
		return findPointToStay(loc.x, loc.y, loc.z, radiusmin, radiusmax, obj.getGeoIndex());
	}

	public static Location findPointToStay(GameObject obj, int radiusmin, int radiusmax)
	{
		return findPointToStay(obj, obj.getLoc(), radiusmin, radiusmax);
	}

	public static Location findPointToStay(GameObject obj, int radius)
	{
		return findPointToStay(obj, 0, radius);
	}

	public static Location coordsRandomize(Location loc, int radiusmin, int radiusmax)
	{
		return coordsRandomize(loc.x, loc.y, loc.z, loc.h, radiusmin, radiusmax);
	}

	public static Location coordsRandomize(int x, int y, int z, int heading, int radiusmin, int radiusmax)
	{
		if(radiusmax == 0 || radiusmax < radiusmin)
			return new Location(x, y, z, heading);
		int radius = Rnd.get(radiusmin, radiusmax);
		double angle = Rnd.nextDouble() * 2 * Math.PI;
		return new Location((int) (x + radius * Math.cos(angle)), (int) (y + radius * Math.sin(angle)), z, heading);
	}

	public static Location findNearest(Creature creature, Location[] locs)
	{
		Location defloc = null;
		for(Location loc : locs)
		{
			if(defloc == null)
				defloc = loc;
			else if(creature.getDistance(loc) < creature.getDistance(defloc))
				defloc = loc;
		}
		return defloc;
	}

	public static int getRandomHeading()
	{
		return Rnd.get(65535);
	}

	@Override
	public Location getRandomLoc(int ref)
	{
		return this;
	}
}