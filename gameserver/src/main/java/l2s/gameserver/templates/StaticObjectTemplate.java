package l2s.gameserver.templates;

import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.model.instances.ChairInstance;
import l2s.gameserver.model.instances.StaticObjectInstance;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 22:33/09.03.2011
 */
public class StaticObjectTemplate
{
	private final int _uid;
	private final int _type; // 0 - signs, 1 - throne, 2 - starter town map, 3 - airship control key
	private final String _filePath;
	private final int _mapX;
	private final int _mapY;
	private final String _name;
	private final int _x;
	private final int _y;
	private final int _z;
	private final boolean _spawn;

	public StaticObjectTemplate(StatsSet set)
	{
		_uid = set.getInteger("uid");
		_type = set.getInteger("stype");
		_mapX = set.getInteger("map_x");
		_mapY = set.getInteger("map_y");
		_filePath = set.getString("path");
		_name = set.getString("name");
		_x = set.getInteger("x");
		_y = set.getInteger("y");
		_z = set.getInteger("z");
		_spawn = set.getBool("spawn");
	}

	public int getUId()
	{
		return _uid;
	}

	public int getType()
	{
		return _type;
	}

	public String getFilePath()
	{
		return _filePath;
	}

	public int getMapX()
	{
		return _mapX;
	}

	public int getMapY()
	{
		return _mapY;
	}

	public String getName()
	{
		return _name;
	}

	public int getX()
	{
		return _x;
	}

	public int getY()
	{
		return _y;
	}

	public int getZ()
	{
		return _z;
	}

	public boolean isSpawn()
	{
		return _spawn;
	}

	public StaticObjectInstance newInstance()
	{
		StaticObjectInstance instance;
		if(getType() == 1)
			instance = new ChairInstance(IdFactory.getInstance().getNextId(), this);
		else
			instance = new StaticObjectInstance(IdFactory.getInstance().getNextId(), this);

		instance.spawnMe(new Location(getX(), getY(), getZ()));

		return instance;
	}
}