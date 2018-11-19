package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.templates.dailymissions.DailyMissionTemplate;
import l2s.gameserver.templates.dailymissions.DailyRewardTemplate;

/**
 * @author Bonux
 **/
public final class DailyMissionsHolder extends AbstractHolder
{
	private static final DailyMissionsHolder _instance = new DailyMissionsHolder();

	private final TIntObjectMap<DailyMissionTemplate> _missions = new TIntObjectHashMap<DailyMissionTemplate>();
	private final TIntObjectMap<Set<DailyMissionTemplate>> _missionsByClassId = new TIntObjectHashMap<Set<DailyMissionTemplate>>(ClassId.VALUES.length);

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
		return _missions.valueCollection();
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