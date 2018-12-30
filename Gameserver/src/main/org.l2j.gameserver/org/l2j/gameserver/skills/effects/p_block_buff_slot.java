package org.l2j.gameserver.skills.effects;

import io.github.joealisson.primitive.sets.IntSet;
import io.github.joealisson.primitive.sets.impl.HashIntSet;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.skills.AbnormalType;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class p_block_buff_slot extends Effect
{
	private final IntSet _blockedAbnormalTypes;

	public p_block_buff_slot(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		_blockedAbnormalTypes = new HashIntSet();

		String[] types = template.getParam().getString("abnormal_types", "").split(";");
		for(String type : types)
			_blockedAbnormalTypes.add(AbnormalType.valueOf(type).ordinal());
	}

	@Override
	public boolean checkBlockedAbnormalType(AbnormalType abnormal)
	{
		if(_blockedAbnormalTypes.isEmpty())
			return false;

		return _blockedAbnormalTypes.contains(abnormal.ordinal());
	}
}