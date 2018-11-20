package org.l2j.gameserver.network.l2.s2c;

import java.util.Collection;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.skills.SkillEntry;

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
		writeString(_charName);
		writeInt(_skills.size());
		for(SkillEntry skillEntry : _skills)
		{
			Skill temp = skillEntry.getTemplate();
			writeInt(temp.isActive() || temp.isToggle() ? 0 : 1);
			writeInt(temp.getDisplayLevel());
			writeInt(temp.getDisplayId());
			writeInt(temp.getReuseSkillId());
			writeByte(_targetChar.isUnActiveSkill(temp.getId()) ? 0x01 : 0x00);
			writeByte(0);
		}
		writeInt(0);
	}
}