package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.skills.SkillEntry;

public class GMViewSkillInfoPacket extends L2GameServerPacket
{
	private final String _charName;
	private final Collection<SkillEntry> _skills;
	private final Player _targetChar;

	public GMViewSkillInfoPacket(Player cha)
	{
		_charName = cha.getName();
		_skills = cha.getAllSkills();
		_targetChar = cha;
	}

	@Override
	protected final void writeImpl()
	{
		writeS(_charName);
		writeD(_skills.size());
		for(SkillEntry skillEntry : _skills)
		{
			Skill temp = skillEntry.getTemplate();
			writeD(temp.isActive() || temp.isToggle() ? 0 : 1);
			writeD(temp.getDisplayLevel());
			writeD(temp.getDisplayId());
			writeD(temp.getReuseSkillId());
			writeC(_targetChar.isUnActiveSkill(temp.getId()) ? 0x01 : 0x00);
			writeC(0);
		}
		writeD(0);
	}
}