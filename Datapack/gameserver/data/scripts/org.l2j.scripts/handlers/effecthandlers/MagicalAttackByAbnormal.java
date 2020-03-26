package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Magical Attack By Abnormal effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class MagicalAttackByAbnormal extends AbstractEffect {
	private final double power;
	
	private MagicalAttackByAbnormal(StatsSet params)
	{
		power = params.getDouble("power", 0);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MAGICAL_ATTACK;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (effector.isAlikeDead()) {
			return;
		}
		
		if (isPlayer(effected) && effected.getActingPlayer().isFakeDeath()) {
			effected.stopFakeDeath(true);
		}

		final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
		double damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power, effected.getMDef(), mcrit);
		
		// each buff increase +30%
		damage *= (((effected.getBuffCount() * 0.3) + 1.3) / 4);
		
		effector.doAttack(damage, effected, skill, false, false, mcrit, false);
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new MagicalAttackByAbnormal(data);
		}

		@Override
		public String effectName() {
			return "MagicalAttackByAbnormal";
		}
	}
}