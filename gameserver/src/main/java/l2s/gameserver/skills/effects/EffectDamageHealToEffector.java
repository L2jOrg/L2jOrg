package l2s.gameserver.skills.effects;

import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

import java.util.Collections;
import java.util.List;

/**
 * DOT effect with additional damahe heal gift: if effected player recieves damage, then effector and his pets will recieve HP heal with some percent.
 * Percent of absorbed heal is setted by stat damageHealToEffector.
 *
 * @author Yorie
 */
public class EffectDamageHealToEffector extends t_hp
{
	private class DamageListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
            final List<Servitor> servitors = _healServitors ? getEffector().getServitors() : Collections.emptyList();

			final double hp = (damage * _hpAbsorbPercent / 100) / (servitors.size() + 1);
			final double mp = (damage * _mpAbsorbPercent / 100) / (servitors.size() + 1);

			for(Servitor servitor :servitors)
			{
				if(hp > 0)
					servitor.setCurrentHp(servitor.getCurrentHp() + hp, false, true);
				
				if(mp > 0)
					servitor.setCurrentMp(servitor.getCurrentMp() + mp);
			}

			if(hp > 0)
			{
				getEffector().setCurrentHp(getEffector().getCurrentHp() + hp, false, true);
				getEffector().sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(hp)));
			}

			if(mp > 0)
			{
				getEffector().setCurrentMp(getEffector().getCurrentMp() + mp);
				getEffector().sendPacket(new SystemMessagePacket(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(Math.round(mp)));
			}
		}
	}

	private final DamageListener _damageListener;
	private final int _hpAbsorbPercent;
	private final int _mpAbsorbPercent;
	private final boolean _healServitors;

	public EffectDamageHealToEffector(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_damageListener = new DamageListener();
		_hpAbsorbPercent = getTemplate().getParam().getInteger("hp_absorb_percent", 0);
		_mpAbsorbPercent = getTemplate().getParam().getInteger("mp_absorb_percent", 0);
		_healServitors = getTemplate().getParam().getBool("heal_servitors", false);
	}

	@Override
	public void onStart()
	{
		getEffected().addListener(_damageListener);
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected().isDead())
			getEffected().removeListener(_damageListener);

		return super.onActionTime();
	}

	@Override
	public void onExit()
	{
		getEffected().removeListener(_damageListener);
	}
}