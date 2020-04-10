package handlers.effecthandlers.stat;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.stats.BaseStats;

import java.util.EnumSet;

/**
 * @author JoeAlisson
 */
public class BaseStatsModify extends AbstractEffect {

    private final double power;
    private final EnumSet<BaseStats> stats;

    private BaseStatsModify(StatsSet data) {
        power = data.getDouble("power", 0);
        stats = data.getStringAsEnumSet("types", BaseStats.class);
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        stats.forEach(stat -> effected.getStats().mergeAdd(stat.getStat(), power));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new BaseStatsModify(data);
        }

        @Override
        public String effectName() {
            return "base-stats";
        }
    }
}
