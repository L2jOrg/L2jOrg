package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

import static java.util.Objects.nonNull;

/**
 * Mana Heal Over Time effect implementation.
 *
 * @author JoeAlisson
 */
public final class ManaHealOverTime extends AbstractEffect {
	private final double power;
	
	private ManaHealOverTime(StatsSet params) {
		power = params.getDouble("power", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item) {
		if (effected.isDead()) {
			return false;
		}
		
		double mp = effected.getCurrentMp();
		final double maxmp = effected.getMaxRecoverableMp();
		
		// Not needed to set the MP and send update packet if player is already at max MP
		if (mp >= maxmp) {
			return true;
		}

		double power = this.power;
		if (nonNull(item) && (item.isPotion() || item.isElixir())) {
			power += effected.getStats().getValue(Stat.ADDITIONAL_POTION_MP, 0) / getTicks();
		}

		mp += power * getTicksMultiplier();
		mp = Math.min(mp, maxmp);
		effected.setCurrentMp(mp, false);
		effected.broadcastStatusUpdate(effector);
		return skill.isToggle();
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new ManaHealOverTime(data);
		}

		@Override
		public String effectName() {
			return "ManaHealOverTime";
		}
	}
}
