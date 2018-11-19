package l2s.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.AbnormalsComparator;

/**
 * @author Bonux
**/
public class i_dispel_by_slot extends i_abstract_effect
{
	private final AbnormalType _abnormalType;
	private final int _maxAbnormalLvl;
	private final boolean _self;

	public i_dispel_by_slot(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		_abnormalType = template.getParam().getEnum("abnormal_type", AbnormalType.class);
		if(_abnormalType == AbnormalType.none)
			_maxAbnormalLvl = 0;
		else
			_maxAbnormalLvl = template.getParam().getInteger("max_abnormal_level", 0);

		_self = template.getEffectType() == EffectType.i_dispel_by_slot_myself;
	}

	@Override
	public boolean checkCondition()
	{
		if(_maxAbnormalLvl == 0)
			return false;

		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		final Creature target = _self ? getEffector() : getEffected();

		final  List<Abnormal> abnormals = new ArrayList<Abnormal>(target.getAbnormalList().values());
		Collections.sort(abnormals, AbnormalsComparator.getInstance());
		Collections.reverse(abnormals);

		for (Abnormal abnormal : abnormals)
		{
			/*if(!effect.isCancelable())
				continue;*/

			Skill effectSkill = abnormal.getSkill();
			if(effectSkill == null)
				continue;

			if(effectSkill.isToggle())
				continue;

			if(effectSkill.isPassive())
				continue;

			/*if(target.isSpecialEffect(effectSkill))
				continue;*/

			if(abnormal.getAbnormalType() != _abnormalType)
				continue;

			if(_maxAbnormalLvl != -1 && abnormal.getAbnormalLvl() > _maxAbnormalLvl)
				continue;

			abnormal.exit();

			if(!abnormal.isHidden())
				target.sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effectSkill));
		}
	}
}