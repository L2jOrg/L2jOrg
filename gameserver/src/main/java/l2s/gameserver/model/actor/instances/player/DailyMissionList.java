package l2s.gameserver.model.actor.instances.player;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;
import java.util.Collections;

import l2s.gameserver.Config;
import l2s.gameserver.dao.CharacterDailyMissionsDAO;
import l2s.gameserver.data.xml.holder.DailyMissionsHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.dailymissions.DailyMissionStatus;
import l2s.gameserver.templates.dailymissions.DailyMissionTemplate;
import l2s.gameserver.templates.dailymissions.DailyRewardTemplate;
import l2s.gameserver.templates.item.data.ItemData;
import l2s.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 **/
public class DailyMissionList
{
	private static final Logger _log = LoggerFactory.getLogger(DailyMissionList.class);

	private final Player _owner;
	private final TIntObjectMap<DailyMission> _missions = new TIntObjectHashMap<DailyMission>();

	public DailyMissionList(Player owner)
	{
		_owner = owner;
	}

	public void restore()
	{
		CharacterDailyMissionsDAO.getInstance().restore(_owner, _missions);
	}

	public void store()
	{
		CharacterDailyMissionsDAO.getInstance().store(_owner, values());
	}

	public Collection<DailyMission> values()
	{
		return _missions.valueCollection();
	}

	public DailyMission get(DailyMissionTemplate missionTemplate)
	{
		DailyMission mission = _missions.get(missionTemplate.getId());
		if(mission == null)
		{
			mission = new DailyMission(_owner, missionTemplate, false, 0);
			_missions.put(mission.getId(), mission);
		}
		return mission;
	}

	public Collection<DailyMissionTemplate> getAvailableMissions()
	{
		if(!Config.EX_USE_TO_DO_LIST)
			return Collections.emptyList();
		return DailyMissionsHolder.getInstance().getMissions(_owner.getBaseClassId());
	}

	public boolean complete(int missionId)
	{
		DailyMissionTemplate missionTemplate = DailyMissionsHolder.getInstance().getMission(missionId);
		if(missionTemplate == null)
			return false;

		DailyMission mission = get(missionTemplate);
		if(mission.getStatus() != DailyMissionStatus.AVAILABLE)
			return false;

		if(_owner.getWeightPenalty() >= 3 || _owner.getInventoryLimit() * 0.8 < _owner.getInventory().getSize())
		{
			_owner.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
			return false;
		}

		int missionValue = mission.getValue();

		mission.setValue((int) (System.currentTimeMillis() / 1000));
		mission.setCompleted(true);

		if(!CharacterDailyMissionsDAO.getInstance().insert(_owner, mission))
		{
			mission.setValue(missionValue);
			mission.setCompleted(false);
			return false;
		}

		for(DailyRewardTemplate reward : missionTemplate.getRewards())
		{
			if(reward.containsClassId(_owner.getBaseClassId()))
			{
				for(ItemData item : reward.getRewardItems())
					ItemFunctions.addItem(_owner, item.getId(), item.getCount());
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "DailyMissionList[owner=" + _owner.getName() + "]";
	}
}