package l2s.gameserver.skills.effects;

import l2s.gameserver.listener.actor.OnAttackListener;
import l2s.gameserver.listener.actor.OnMagicUseListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * reworked by Bonux
**/
public final class EffectDispelOnHit extends Effect
{
	private class AttackListener implements OnAttackListener, OnMagicUseListener
	{
		@Override
		public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt)
		{
			// TODO: [Bonux] Проверить, должен ли распространяться эффект на магию. 
			if(!skill.isOffensive())
				return;

			EffectDispelOnHit.this.onAttack();
		}

		@Override
		public void onAttack(Creature actor, Creature target)
		{
			EffectDispelOnHit.this.onAttack();
		}
	}

	private final int _maxHitCount;

	private AttackListener _listener;
	private int _hitCount = 0;

	public EffectDispelOnHit(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_maxHitCount = getTemplate().getParam().getInteger("max_hits", 0);
	}

	private void onAttack()
	{
		_hitCount++;
		if(_hitCount >= _maxHitCount)
			getEffected().getAbnormalList().stop(getSkill());
	}

	@Override
	public void onStart()
	{
		_listener = new AttackListener();

		getEffected().addListener(_listener);
	}

	@Override
	public void onExit()
	{
		getEffected().removeListener(_listener);
	}
}