package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.s2c.SkillCoolTimePacket;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public final class i_reset_skill_reuse extends i_abstract_effect
{
	private final int _skillId;

	public i_reset_skill_reuse(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_skillId = template.getParam().getInteger("id");
	}

	@Override
	public void instantUse()
	{
		SkillEntry skill = getEffected().getKnownSkill(_skillId);
		if(skill != null)
		{
			getEffected().enableSkill(skill.getTemplate());
			if(getEffected().isPlayer())
			{
				Player player = getEffected().getPlayer();
				player.sendPacket(new SkillCoolTimePacket(player));
			}
		}
	}
}