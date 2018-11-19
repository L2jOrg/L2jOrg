package l2s.gameserver.skills.effects;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;

import l2s.commons.lang.reference.HardReference;
import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectCurseOfLifeFlow extends Effect
{
	private CurseOfLifeFlowListener _listener;

	private TObjectIntHashMap<HardReference<? extends Creature>> _damageList = new TObjectIntHashMap<HardReference<? extends Creature>>();

	public EffectCurseOfLifeFlow(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		_listener = new CurseOfLifeFlowListener();
		getEffected().addListener(_listener);
	}

	@Override
	public void onExit()
	{
		getEffected().removeListener(_listener);
		_listener = null;
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected().isDead())
			return false;

		for(TObjectIntIterator<HardReference<? extends Creature>> iterator = _damageList.iterator(); iterator.hasNext();)
		{
			iterator.advance();
			Creature damager = iterator.key().get();
			if(damager == null || damager.isDead() || damager.isCurrentHpFull())
				continue;

			int damage = iterator.value();
			if(damage <= 0)
				continue;

			double max_heal = getValue();
			double heal = Math.min(damage, max_heal);
			double newHp = Math.min(damager.getCurrentHp() + heal, damager.getMaxHp());

			if(damager != getEffector())
				damager.sendPacket(new SystemMessagePacket(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addName(getEffector()).addLong((long) (newHp - damager.getCurrentHp())));
			else
				damager.sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addLong((long) (newHp - damager.getCurrentHp())));
			damager.setCurrentHp(newHp, false);
		}

		_damageList.clear();

		return true;
	}

	private class CurseOfLifeFlowListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			if(attacker == actor || attacker == getEffected())
				return;
			int old_damage = _damageList.get(attacker.getRef());
			_damageList.put(attacker.getRef(), old_damage == 0 ? (int) damage : old_damage + (int) damage);
		}
	}
}