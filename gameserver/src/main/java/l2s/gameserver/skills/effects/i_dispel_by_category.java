package l2s.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.AbnormalsComparator;

/**
 * @author Bonux
**/
public class i_dispel_by_category extends i_abstract_effect
{
	private static enum AbnormalCategory
	{
		slot_buff,
		slot_debuff
	}

	private final AbnormalCategory _abnormalCategory;
	private final int _dispelChance;
	private final int _maxCount;

	public i_dispel_by_category(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		_abnormalCategory = template.getParam().getEnum("abnormal_category", AbnormalCategory.class);
		_dispelChance = template.getParam().getInteger("dispel_chance", 100);
		_maxCount = template.getParam().getInteger("max_count", 0);
	}

	@Override
	public boolean checkCondition()
	{
		if(_dispelChance == 0 || _maxCount == 0)
			return false;

		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		final List<Abnormal> effects = new ArrayList<Abnormal>(getEffected().getAbnormalList().values());
		Collections.sort(effects, AbnormalsComparator.getInstance());
		Collections.reverse(effects);

		int dispelled;
		if(_abnormalCategory == AbnormalCategory.slot_debuff)
		{
			dispelled = 0;
			for(Abnormal abnormal : effects)
			{
				if(!abnormal.isCancelable())
					continue;

				Skill effectSkill = abnormal.getSkill();
				if(effectSkill == null)
					continue;

				if(!abnormal.isOffensive())
					continue;

				if(effectSkill.isToggle())
					continue;

				if(effectSkill.isPassive())
					continue;

				if(getEffected().isSpecialAbnormal(effectSkill))
					continue;

				if(effectSkill.getMagicLevel() <= 0)
					continue;

				if(Rnd.chance(_dispelChance))
				{
					abnormal.exit();

					if(!abnormal.isHidden())
						getEffected().sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effectSkill));

					dispelled++;

					if(_maxCount > 0 && dispelled >= _maxCount)
						break;
				}
			}
		}
		else if(_abnormalCategory == AbnormalCategory.slot_buff)
		{
			dispelled = 0;
			for(Abnormal abnormal : effects)
			{
				if(!abnormal.isCancelable())
					continue;

				Skill effectSkill = abnormal.getSkill();
				if(effectSkill == null)
					continue;

				if(abnormal.isOffensive())
					continue;

				if(effectSkill.isToggle())
					continue;

				if(effectSkill.isPassive())
					continue;

				if(getEffected().isSpecialAbnormal(effectSkill))
					continue;

				if(effectSkill.getMagicLevel() <= 0)
					continue;

				if(calcCancelChance(abnormal))
				{
					abnormal.exit();

					if(!abnormal.isHidden())
						getEffected().sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effectSkill));

					dispelled++;

					if(_maxCount > 0 && dispelled >= _maxCount)
						break;
				}
			}
		}
	}

	private boolean calcCancelChance(Abnormal abnormal)
	{
		double chance = Formulas.calcCancelChance(getEffector(), getEffected(), _dispelChance, getSkill(), abnormal);
		return Rnd.chance(chance);
	}
}