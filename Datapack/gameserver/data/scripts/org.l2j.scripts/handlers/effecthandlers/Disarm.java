package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Disarm effect implementation.
 * @author nBd
 */
public final class Disarm extends AbstractEffect {
	public Disarm(StatsSet params) {
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
}
