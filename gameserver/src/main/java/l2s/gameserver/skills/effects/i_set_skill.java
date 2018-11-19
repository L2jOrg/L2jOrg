package l2s.gameserver.skills.effects;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 */
public final class i_set_skill extends i_abstract_effect
{
	private final SkillEntry _skill;

	public i_set_skill(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		int[] skill = template.getParam().getIntegerArray("skill", "-");
		_skill = SkillHolder.getInstance().getSkillEntry(skill[0], skill.length >= 2 ? skill[1] : 1);
	}

	@Override
	public boolean checkCondition()
	{
		if(_skill == null)
			return false;

		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		Player player = getEffected().getPlayer();
		player.addSkill(_skill, true);
		player.updateStats();
		player.sendSkillList();
		player.updateSkillShortcuts(_skill.getId(), _skill.getLevel());
	}
}