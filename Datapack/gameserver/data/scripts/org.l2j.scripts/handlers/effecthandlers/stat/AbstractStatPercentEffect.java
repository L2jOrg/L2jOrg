package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author JoeAlisson
 */
public class AbstractStatPercentEffect extends AbstractEffect {

    private final Stat stat;
    private final int amount;

    public AbstractStatPercentEffect(StatsSet params, Stat stat) {
        this.stat = stat;
        amount = params.getInt("amount", 0);
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        effected.getStats().mergeMul(stat, 1 + amount / 100.);
    }
}
