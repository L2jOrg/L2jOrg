package l2s.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.AbnormalsComparator;

/**
 * @author Bonux
**/
public class i_dispel_by_slot_probability extends i_abstract_effect
{
	private final AbnormalType _abnormalType;
	private final int _dispelChance;

	public i_dispel_by_slot_probability(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		_abnormalType = template.getParam().getEnum("abnormal_type", AbnormalType.class);
		if(_abnormalType == AbnormalType.none)
			_dispelChance = 0;
		else
			_dispelChance = template.getParam().getInteger("dispel_chance", 100);
	}

	@Override
	public boolean checkCondition()
	{
		if(_dispelChance == 0)
			return false;

		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		final List<Abnormal> abnormals = new ArrayList<Abnormal>(getEffected().getAbnormalList().values());
		Collections.sort(abnormals, AbnormalsComparator.getInstance());
		Collections.reverse(abnormals);

		for(Abnormal abnormal : abnormals)
		{
			if(!abnormal.isCancelable())
				continue;

			Skill skill = abnormal.getSkill();
			if(skill == null)
				continue;

			if(skill.isToggle())
				continue;

			if(skill.isPassive())
				continue;

			/*if(getEffected().isSpecialEffect(effectSkill))
				continue;*/

			if(abnormal.getAbnormalType() != _abnormalType)
				continue;

			if(Rnd.chance(_dispelChance))
			{
				abnormal.exit();

				if(!abnormal.isHidden())
					getEffected().sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(skill));
			}
		}
	}
}