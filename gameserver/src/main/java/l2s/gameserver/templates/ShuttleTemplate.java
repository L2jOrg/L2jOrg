package l2s.gameserver.templates;

import java.util.Collection;
import java.util.TreeMap;

/**
 * @author Bonux
**/
public final class ShuttleTemplate extends CreatureTemplate
{
	public static class ShuttleDoor
	{
		private final int _id;
		public final int[] unkParam = new int[9];

		public ShuttleDoor(int id, StatsSet set)
		{
			_id = id;
			unkParam[0] = set.getInteger("unk_param_0", 0);
			unkParam[1] = set.getInteger("unk_param_1", 0);
			unkParam[2] = set.getInteger("unk_param_2", 0);
			unkParam[3] = set.getInteger("unk_param_3", 0);
			unkParam[4] = set.getInteger("unk_param_4", 0);
			unkParam[5] = set.getInteger("unk_param_5", 0);
			unkParam[6] = set.getInteger("unk_param_6", 0);
			unkParam[7] = set.getInteger("unk_param_7", 0);
			unkParam[8] = set.getInteger("unk_param_8", 0);
		}

		public int getId()
		{
			return _id;
		}
	}

	private final int _id;
	private final TreeMap<Integer, ShuttleDoor> _doors = new TreeMap<Integer, ShuttleDoor>();

	public ShuttleTemplate(int id)
	{
		super(CreatureTemplate.getEmptyStatsSet());
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public Collection<ShuttleDoor> getDoors()
	{
		return _doors.values();
	}

	public ShuttleDoor getDoor(int id)
	{
		return _doors.get(id);
	}

	public void addDoor(ShuttleDoor door)
	{
		_doors.put(door.getId(), door);
	}
}