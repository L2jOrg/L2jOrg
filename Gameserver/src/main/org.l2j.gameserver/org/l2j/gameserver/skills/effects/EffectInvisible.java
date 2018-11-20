package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public final class EffectInvisible extends Effect
{
	public EffectInvisible(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isPlayer())
		{
			Player player = getEffected().getPlayer();
			if(player.getActiveWeaponFlagAttachment() != null)
				return false;
		}
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		if(!getEffected().isPlayer())
			return;

		getEffected().startInvisible(this, true);
	}

	@Override
	public void onExit()
	{
		if(getEffected().isPlayer())
		{
			getEffected().stopInvisible(this, true);

			for(Servitor servitor : getEffected().getServitors())
				servitor.getAbnormalList().stop(getSkill());
		}
		else if(getEffected().isServitor())
			getEffected().getPlayer().getAbnormalList().stop(getSkill());
	}
}