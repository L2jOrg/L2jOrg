package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.network.l2.GameClient;
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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_charName, buffer);
		buffer.putInt(_skills.size());
		for(SkillEntry skillEntry : _skills)
		{
			Skill temp = skillEntry.getTemplate();
			buffer.putInt(temp.isActive() || temp.isToggle() ? 0 : 1);
			buffer.putInt(temp.getDisplayLevel());
			buffer.putInt(temp.getDisplayId());
			buffer.putInt(temp.getReuseSkillId());
			buffer.put((byte) (_targetChar.isUnActiveSkill(temp.getId()) ? 0x01 : 0x00));
			buffer.put((byte)0);
		}
		buffer.putInt(0);
	}
}