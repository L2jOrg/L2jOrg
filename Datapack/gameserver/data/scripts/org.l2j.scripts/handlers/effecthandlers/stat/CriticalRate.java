package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class CriticalRate extends AbstractConditionalHpEffect {

    public CriticalRate(StatsSet params) {
        super(params, Stat.CRITICAL_RATE);
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        if (conditions.isEmpty() || conditions.stream().allMatch(cond -> cond.test(effected, effected, skill))) {
            switch (mode) {
                case DIFF -> effected.getStats().mergeAdd(addStat, power);
                case PER -> effected.getStats().mergeMul(mulStat, (power / 100));
            }
        }
    }
}
