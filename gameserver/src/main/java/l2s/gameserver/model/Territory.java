package l2s.gameserver.model;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.geometry.Point3D;
import l2s.commons.geometry.Shape;
import l2s.commons.util.Rnd;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.templates.spawn.SpawnRange;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

/**
 * Описание территории мира.
 * Содержит список границ включающих в себя территорию и исключающихся из территории.
 * 
 * @author G1ta0
 */
public class Territory implements Shape, SpawnRange
{
	protected final Point3D max = new Point3D();
	protected final Point3D min = new Point3D();

	private final List<Shape> include = new ArrayList<Shape>(1);
	private final List<Shape> exclude = new ArrayList<Shape>(1);

	public Territory()
	{

	}

	public Territory add(Shape shape)
	{
		if(include.isEmpty())
		{
			max.x = shape.getXmax();
			max.y = shape.getYmax();
			max.z = shape.getZmax();
			min.x = shape.getXmin();
			min.y = shape.getYmin();
			min.z = shape.getZmin();
		}
		else
		{
			max.x = Math.max(max.x, shape.getXmax());
			max.y = Math.max(max.y, shape.getYmax());
			max.z = Math.max(max.z, shape.getZmax());
			min.x = Math.min(min.x, shape.getXmin());
			min.y = Math.min(min.y, shape.getYmin());
			min.z = Math.min(min.z, shape.getZmin());
		}

		include.add(shape);
		return this;
	}

	public Territory addBanned(Shape shape)
	{
		exclude.add(shape);
		return this;
	}

	public List<Shape> getTerritories()
	{
		return include;
	}

	public List<Shape> getBannedTerritories()
	{
		return exclude;
	}

	@Override
	public boolean isInside(int x, int y)
	{
		Shape shape;
		for(int i = 0; i < include.size(); i++)
		{
			shape = include.get(i);
			if(shape.isInside(x, y))
				return !isExcluded(x, y);
		}
		return false;
	}

	@Override
	public boolean isInside(int x, int y, int z)
	{
		if(x < this.min.x || x > this.max.x || y < this.min.y || y > this.max.y || z < this.min.z || z > this.max.z)
			return false;

		Shape shape;
		for(int i = 0; i < include.size(); i++)
		{
			shape = include.get(i);
			if(shape.isInside(x, y, z))
				return !isExcluded(x, y, z);
		}
		return false;
	}

	@Override
	public boolean isOnPerimeter(int x, int y)
	{
		return isInside(x, y); // TODO
	}

	@Override
	public boolean isOnPerimeter(int x, int y, int z)
	{
		return isInside(x, y, z); // TODO
	}

	public boolean isInside(GameObject obj)
	{
		return isInside(obj.getLoc());
	}

	public boolean isInside(Location loc)
	{
		return isInside(loc.x, loc.y, loc.z);
	}

	public boolean isExcluded(int x, int y)
	{
		Shape shape;
		for(int i = 0; i < exclude.size(); i++)
		{
			shape = exclude.get(i);
			if(shape.isInside(x, y))
				return true;
		}
		return false;
	}

	public boolean isExcluded(int x, int y, int z)
	{
		Shape shape;
		for(int i = 0; i < exclude.size(); i++)
		{
			shape = exclude.get(i);
			if(shape.isInside(x, y, z))
				return true;
		}
		return false;
	}

	@Override
	public int getXmax()
	{
		return this.max.x;
	}

	@Override
	public int getXmin()
	{
		return this.min.x;
	}

	@Override
	public int getYmax()
	{
		return this.max.y;
	}

	@Override
	public int getYmin()
	{
		return this.min.y;
	}

	@Override
	public int getZmax()
	{
		return this.max.z;
	}

	@Override
	public int getZmin()
	{
		return this.min.z;
	}

	public static Location getRandomLoc(Territory territory)
	{
		return getRandomLoc(territory, 0);
	}

	public static Location getRandomLoc(Territory territory, int geoIndex)
	{
		Location pos = new Location();

		List<Shape> territories = territory.getTerritories();

		loop: for(int i = 0; i < 100; i++)
		{
			Shape shape = territories.get(Rnd.get(territories.size()));

			pos.x = Rnd.get(shape.getXmin(), shape.getXmax());
			pos.y = Rnd.get(shape.getYmin(), shape.getYmax());
			pos.z = shape.getZmin() + (shape.getZmax() - shape.getZmin()) / 2;

			if(territory.isInside(pos.x, pos.y))
			{
				// Не спаунить в колонны, стены и прочее.
				int tempz = GeoEngine.getHeight(pos, geoIndex);
				if(shape.getZmin() != shape.getZmax())
				{
					if(tempz < shape.getZmin() || tempz > shape.getZmax())
						continue;
				}
				else if(tempz < shape.getZmin() - 200 || tempz > shape.getZmin() + 200)
					continue;

				pos.z = tempz;

				int geoX = pos.x - World.MAP_MIN_X >> 4;
				int geoY = pos.y - World.MAP_MIN_Y >> 4;

				// Если местность подозрительная - пропускаем
				for(int x = geoX - 1; x <= geoX + 1; x++)
				{
					for(int y = geoY - 1; y <= geoY + 1; y++)
					{
						if(GeoEngine.NgetNSWE(x, y, tempz, geoIndex) != GeoEngine.NSWE_ALL)
							continue loop;
					}
				}
				pos.h = Rnd.get(64000);
				return pos;
			}
		}
		pos.h = Rnd.get(64000);
		return pos;
	}

	public double getDistance(Location loc)
	{
		return PositionUtils.getDistance((getXmin() + getXmax()) / 2, (getYmin() + getYmax()) / 2, loc.x, loc.y);
	}

	@Override
	public Location getRandomLoc(int geoIndex)
	{
		return getRandomLoc(this, geoIndex);
	}
}