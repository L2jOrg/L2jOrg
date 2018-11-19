package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.skills.TimeStamp;

public class SkillCoolTimePacket extends L2GameServerPacket
{
	private List<Skill> _list = Collections.emptyList();

	public SkillCoolTimePacket(Player player)
	{
		Collection<TimeStamp> list = player.getSkillReuses();
		_list = new ArrayList<Skill>(list.size());
		for(TimeStamp stamp : list)
		{
			if(!stamp.hasNotPassed())
				continue;
			SkillEntry skillEntry = player.getKnownSkill(stamp.getId());
			if(skillEntry == null)
				continue;
			Skill sk = new Skill();
			sk.skillId = skillEntry.getId();
			sk.level = skillEntry.getLevel();
			sk.reuseBase = (int) Math.round(stamp.getReuseBasic() / 1000.);
			sk.reuseCurrent = (int) Math.round(stamp.getReuseCurrent() / 1000.);
			_list.add(sk);
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_list.size()); //Size of list
		for(int i = 0; i < _list.size(); i++)
		{
			Skill sk = _list.get(i);
			writeD(sk.skillId); //Skill Id
			writeD(sk.level); //Skill Level
			writeD(sk.reuseBase); //Total reuse delay, seconds
			writeD(sk.reuseCurrent); //Time remaining, seconds
		}
	}

	private static class Skill
	{
		public int skillId;
		public int level;
		public int reuseBase;
		public int reuseCurrent;
	}
}