package org.l2j.gameserver.templates.player;

import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
 */
public class ClassData
{
	private final int _classId;

	private final HashIntObjectMap<HpMpCpData> _hpMpCpData = new HashIntObjectMap<HpMpCpData>();

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