package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 */
public final class i_refresh_instance extends i_abstract_effect
{
	public i_refresh_instance(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;

		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		Player player = getEffected().getPlayer();
		if(player != null)
		{
			int instanceId = (int) getValue();
			if(instanceId == -1)
				player.removeAllInstanceReuses();
			else
				player.removeInstanceReuse(instanceId);
		}
	}
}