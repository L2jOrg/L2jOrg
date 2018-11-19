package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.DailyMission;
import l2s.gameserver.templates.dailymissions.DailyMissionTemplate;

public class ExOneDayReceiveRewardList extends L2GameServerPacket
{
	private static final SchedulingPattern DAILY_REUSE_PATTERN = new SchedulingPattern("30 6 * * *");
	private static final SchedulingPattern WEEKLY_REUSE_PATTERN = new SchedulingPattern("30 6 * * 1");
	private static final SchedulingPattern MONTHLY_REUSE_PATTERN = new SchedulingPattern("30 6 1 * *");

	private final int _dayRemainTime;
	private final int _weekRemainTime;
	private final int _monthRemainTime;
	private final int _classId;
	private final int _dayOfWeek;
	private final List<DailyMission> _missions = new ArrayList<DailyMission>();

	public ExOneDayReceiveRewardList(Player player)
	{
		_dayRemainTime = (int) ((DAILY_REUSE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis()) / 1000L);
		_weekRemainTime = (int) ((WEEKLY_REUSE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis()) / 1000L);
		_monthRemainTime = (int) ((MONTHLY_REUSE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis()) / 1000L);
		_classId = player.getBaseClassId();
		_dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

		for(DailyMissionTemplate missionTemplate : player.getDailyMissionList().getAvailableMissions())
		{
			DailyMission mission = player.getDailyMissionList().get(missionTemplate);
			if(!mission.isFinallyCompleted())
				_missions.add(mission);
		}
		Collections.sort(_missions);
	}

	public ExOneDayReceiveRewardList()
	{
		_dayRemainTime = (int) ((DAILY_REUSE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis()) / 1000L);
		_weekRemainTime = (int) ((WEEKLY_REUSE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis()) / 1000L);
		_monthRemainTime = (int) ((MONTHLY_REUSE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis()) / 1000L);
		_classId = 0;
		_dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}

	@Override
	protected void writeImpl()
	{
		writeD(_dayRemainTime);
		writeD(_weekRemainTime);
		writeD(_monthRemainTime);
		writeC(0x14);
		writeD(_classId);
		writeD(_dayOfWeek);
		writeD(_missions.size());
		for(DailyMission mission : _missions)
		{
			writeH(mission.getId());
			writeC(mission.getStatus().ordinal());
			writeC(0x01);
			writeD(mission.getCurrentProgress());
			writeD(mission.getRequiredProgress());
		}
	}
}