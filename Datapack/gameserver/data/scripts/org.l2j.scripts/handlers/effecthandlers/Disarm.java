package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Disarm effect implementation.
 * @author nBd
 * @author JoeAlisson
 */
public final class Disarm extends AbstractEffect {
	private Disarm() {
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return isPlayer(effected);
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.DISARMED.getMask();
	}
	
	@Override
	public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item) {
		final Player player = effected.getActingPlayer();
		if (nonNull(player)) {
			player.disarmWeapons();
		}
	}

	public static class Factory implements SkillEffectFactory {
		private static final Disarm INSTANCE = new Disarm();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "Disarm";
		}
	}
}
