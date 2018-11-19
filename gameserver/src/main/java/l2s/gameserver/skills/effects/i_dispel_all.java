package l2s.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.AbnormalsComparator;

/**
 * @author Bonux
**/
public class i_dispel_all extends i_abstract_effect
{
	public i_dispel_all(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void instantUse()
	{
        final List<Abnormal> abnormals = new ArrayList<Abnormal>(getEffected().getAbnormalList().values());
        Collections.sort(abnormals, AbnormalsComparator.getInstance());
        Collections.reverse(abnormals);

        for (Abnormal abnormal : abnormals)
		{
			if(!abnormal.isCancelable())
				continue;

			Skill effectSkill = abnormal.getSkill();
			if(effectSkill == null)
				continue;

			if(effectSkill.isToggle())
				continue;

			if(effectSkill.isPassive())
				continue;

			if(getEffected().isSpecialAbnormal(effectSkill))
				continue;

			abnormal.exit();

			if(!abnormal.isHidden())
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effectSkill));
		}
	}
}