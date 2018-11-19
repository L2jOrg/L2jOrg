package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class c_mp_by_level extends Effect
{
	public c_mp_by_level(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected().isDead())
			return false;

		double base = getValue() * getInterval();
		double consume = getSkill().getAbnormalTime() > 0 ? (getEffected().getLevel() - 1) / 7.5 * base * getSkill().getAbnormalTime() : base;
		if(consume > getEffected().getCurrentMp())
		{
			getEffected().sendPacket(SystemMsg.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
			return false;
		}

		getEffected().reduceCurrentMp(consume, null);
		return getSkill().isToggle();
	}
}