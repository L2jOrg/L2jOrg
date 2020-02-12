package handlers.effecthandlers;

import handlers.effecthandlers.stat.AbstractStatEffect;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

public class StatModify extends AbstractStatEffect {

    public StatModify(StatsSet params) {
        super(params, params.getEnum("stat", Stat.class), addStatOrStat(params));
    }

    private static Stat addStatOrStat(StatsSet params) {
        if(params.contains("stat-add")) {
            return params.getEnum("stat-add", Stat.class);
        }
        return params.getEnum("stat", Stat.class);
    }
}
