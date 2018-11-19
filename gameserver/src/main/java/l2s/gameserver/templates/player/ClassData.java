package l2s.gameserver.templates.player;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Bonux
 */
public class ClassData
{
	private final int _classId;

	private final TIntObjectHashMap<HpMpCpData> _hpMpCpData = new TIntObjectHashMap<HpMpCpData>();

	public ClassData(int classId)
	{
		_classId = classId;
	}

	public void addHpMpCpData(int level, double hp, double mp, double cp)
	{
		_hpMpCpData.put(level, new HpMpCpData(hp, mp, cp));
	}

	public HpMpCpData getHpMpCpData(int level)
	{
		return _hpMpCpData.get(level);
	}

	public int getClassId()
	{
		return _classId;
	}
}