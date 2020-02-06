package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.taskmanager.DecayTaskManager;

import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Resurrection effect implementation.
 * @author Adry_85
 */
public final class Resurrection extends AbstractEffect {

	public final int power;
	
	public Resurrection(StatsSet params)
	{
		power = params.getInt("power", 0);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.RESURRECTION;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (isPlayer(effector)) {
			final Player player = effected.getActingPlayer();
			if (!player.isResurrectionBlocked() && !player.isReviveRequested()) {
				effected.getActingPlayer().reviveRequest(effector.getActingPlayer(), skill, isPet(effected), power);
			}
		} else {
			DecayTaskManager.getInstance().cancel(effected);
			effected.doRevive(Formulas.calculateSkillResurrectRestorePercent(power, effector));
		}
	}
}