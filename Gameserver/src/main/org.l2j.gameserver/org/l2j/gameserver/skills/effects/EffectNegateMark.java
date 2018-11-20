package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.skills.EffectType;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.stats.Formulas;
import org.l2j.gameserver.stats.Formulas.AttackInfo;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectNegateMark extends Effect
{
	private static final int MARK_OF_WEAKNESS = 11259;
	private static final int MARK_OF_PLAGUE = 11261;
	private static final int MARK_OF_TRICK = 11262;

	public EffectNegateMark(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		Creature effected = getEffected();
		Creature effector = getEffector();
		Skill skill = getSkill();

		byte markCount = 0;

		for(Abnormal abnormal : effected.getAbnormalList())
		{
            int skillId = abnormal.getSkill().getId();
            if((skillId == MARK_OF_WEAKNESS) || (skillId == MARK_OF_PLAGUE) || (skillId == MARK_OF_TRICK))
            {
                markCount = (byte) (markCount + 1);
                effected.getAbnormalList().stop(skillId);
            }
		}

		if(markCount > 0)
		{
			AttackInfo info = Formulas.calcMagicDam(effector, effected, skill, getSkill().isSSPossible());
			effected.reduceCurrentHp(info.damage * markCount, effector, skill, true, true, false, true, false, false, true, true, info.crit, info.miss, info.shld);
		}
	}
}
