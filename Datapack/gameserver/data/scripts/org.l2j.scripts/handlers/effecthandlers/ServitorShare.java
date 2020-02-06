package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Servitor Share effect implementation.
 */
public final class ServitorShare extends AbstractEffect {
	public final Map<Stat, Float> sharedStats = new HashMap<>();
	
	public ServitorShare(StatsSet params) {
		if (params.isEmpty()) {
			return;
		}
		
		for (Entry<String, Object> param : params.getSet().entrySet()) {
			sharedStats.put(Stat.valueOf(param.getKey()), (Float.parseFloat((String) param.getValue())) / 100);
		}
	}
	
	@Override
	public boolean canPump(Creature effector, Creature effected, Skill skill)
	{
		return isSummon(effected);
	}
	
	@Override
	public void pump(Creature effected, Skill skill) {
		final Player owner = effected.getActingPlayer();
		if (nonNull(owner)) {
			for (Entry<Stat, Float> stats : sharedStats.entrySet()) {
				effected.getStats().mergeAdd(stats.getKey(), owner.getStats().getValue(stats.getKey()) * stats.getValue());
			}
		}
	}
}