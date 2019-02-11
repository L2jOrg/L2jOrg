package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.skills.SkillEntry;

/**
 * format   d (dddc)
			d  dddcc
 */
public class SkillListPacket extends L2GameServerPacket
{
	private final Collection<SkillEntry> _skills;
	private final Player _player;
	private final int _learnedSkillId;

	public SkillListPacket(Player player)
	{
		_skills = player.getAllSkills();
		_player = player;
		_learnedSkillId = 0;
	}

	public SkillListPacket(Player player, int learnedSkillId)
	{
		_skills = player.getAllSkills();
		_player = player;
		_learnedSkillId = learnedSkillId;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_skills.size());
		for(SkillEntry skillEntry : _skills)
		{
			Skill temp = skillEntry.getTemplate();
			buffer.putInt(temp.isActive() || temp.isToggle() ? 0 : 1); // deprecated? клиентом игнорируется
			buffer.putInt(temp.getDisplayLevel());
			buffer.putInt(temp.getDisplayId());
			buffer.putInt(temp.getReuseSkillId());
			buffer.put((byte) (_player.isUnActiveSkill(temp.getId()) ? 0x01 : 0x00)); // иконка скилла серая если не 0
			buffer.put((byte)0x00); // для заточки: если 1 скилл можно точить
		}
		buffer.putInt(_learnedSkillId);
	}
}