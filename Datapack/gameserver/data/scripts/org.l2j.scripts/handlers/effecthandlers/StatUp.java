package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.BaseStats;

/**
 * @author Sdw
 * @author JoeAlisson
 * */
public class StatUp extends AbstractEffect {
    private final BaseStats stat;
    private final double power;

    private StatUp(StatsSet params) {
        power = params.getDouble("power", 0);
        stat = params.getEnum("type", BaseStats.class, BaseStats.STR);
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        effected.getStats().mergeAdd(stat.getStat(), power);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new StatUp(data);
        }

        @Override
        public String effectName() {
            return "base-stat";
        }
    }
}
