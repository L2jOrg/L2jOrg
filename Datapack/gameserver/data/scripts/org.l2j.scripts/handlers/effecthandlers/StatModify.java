package handlers.effecthandlers;

import handlers.effecthandlers.stat.AbstractStatEffect;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author JoeAlisson
 */
public class StatModify extends AbstractStatEffect {

    private StatModify(StatsSet params) {
        super(params, params.getEnum("stat", Stat.class), addStat(params));
    }

    private static Stat addStat(StatsSet params) {
        if(params.contains("stat-add")) {
            return params.getEnum("stat-add", Stat.class);
        }
        return params.getEnum("stat", Stat.class);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new StatModify(data);
        }

        @Override
        public String effectName() {
            return "stat-modify";
        }
    }
}
