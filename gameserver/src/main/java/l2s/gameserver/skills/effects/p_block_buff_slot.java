package l2s.gameserver.skills.effects;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class p_block_buff_slot extends Effect
{
	private final TIntSet _blockedAbnormalTypes;

	public p_block_buff_slot(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		_blockedAbnormalTypes = new TIntHashSet();

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