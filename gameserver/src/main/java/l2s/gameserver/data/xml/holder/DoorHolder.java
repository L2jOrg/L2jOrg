package l2s.gameserver.data.xml.holder;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.DoorTemplate;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public final class DoorHolder extends AbstractHolder
{
	private static final DoorHolder _instance = new DoorHolder();

	private IntObjectMap<DoorTemplate> _doors = new HashIntObjectMap<DoorTemplate>();

	public static DoorHolder getInstance()
	{
		return _instance;
	}

	public void addTemplate(DoorTemplate door)
	{
		_doors.put(door.getId(), door);
	}

	public DoorTemplate getTemplate(int doorId)
	{
		return _doors.get(doorId);
	}

	public IntObjectMap<DoorTemplate> getDoors()
	{
		return _doors;
	}

	@Override
	public int size()
	{
		return _doors.size();
	}

	@Override
	public void clear()
	{
		_doors.clear();
	}
}