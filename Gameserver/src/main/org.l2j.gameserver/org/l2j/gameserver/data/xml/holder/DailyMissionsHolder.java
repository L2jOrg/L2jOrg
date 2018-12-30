package org.l2j.gameserver.data.xml.holder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.templates.dailymissions.DailyMissionTemplate;
import org.l2j.gameserver.templates.dailymissions.DailyRewardTemplate;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
 **/
public final class DailyMissionsHolder extends AbstractHolder
{
	private static final DailyMissionsHolder _instance = new DailyMissionsHolder();

	private final IntObjectMap<DailyMissionTemplate> _missions = new HashIntObjectMap<DailyMissionTemplate>();
	private final IntObjectMap<Set<DailyMissionTemplate>> _missionsByClassId = new HashIntObjectMap<Set<DailyMissionTemplate>>(ClassId.VALUES.length);

	public static DailyMissionsHolder getInstance()
	{
		return _instance;
	}

	public void addMission(DailyMissionTemplate mission)
	{
		_missions.put(mission.getId(), mission);

		for(DailyRewardTemplate reward : mission.getRewards())
		{
			for(ClassId classId : ClassId.VALUES)
			{
				if(!reward.containsClassId(classId.getId()))
					continue;

				Set<DailyMissionTemplate> missionsByClassId = _missionsByClassId.get(classId.getId());
				if(missionsByClassId == null)
				{
					missionsByClassId = new HashSet<DailyMissionTemplate>();
					_missionsByClassId.put(classId.getId(), missionsByClassId);
				}
				missionsByClassId.add(mission);
			}
		}
	}

	public DailyMissionTemplate getMission(int id)
	{
		return _missions.get(id);
	}

	public Collection<DailyMissionTemplate> getMissions()
	{
		return _missions.values();
	}

	public Collection<DailyMissionTemplate> getMissions(int classId)
	{
		Collection<DailyMissionTemplate> missions = _missionsByClassId.get(classId);
		if(missions == null)
			return Collections.emptyList();
		return missions;
	}

	@Override
	public int size()
	{
		return _missions.size();
	}

	@Override
	public void clear()
	{
		_missions.clear();
	}
}