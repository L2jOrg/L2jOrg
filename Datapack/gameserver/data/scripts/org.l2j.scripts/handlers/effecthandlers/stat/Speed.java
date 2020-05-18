package handlers.effecthandlers.stat;

import handlers.effecthandlers.stat.AbstractStatEffect;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public final class Speed extends AbstractStatEffect {

    private Speed(StatsSet params) {
        super(params, Stat.RUN_SPEED);
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        switch (mode) {
            case DIFF -> {
                effected.getStats().mergeAdd(Stat.RUN_SPEED, power);
                effected.getStats().mergeAdd(Stat.WALK_SPEED, power);
                effected.getStats().mergeAdd(Stat.SWIM_RUN_SPEED, power);
                effected.getStats().mergeAdd(Stat.SWIM_WALK_SPEED, power);
                effected.getStats().mergeAdd(Stat.FLY_RUN_SPEED, power);
                effected.getStats().mergeAdd(Stat.FLY_WALK_SPEED, power);
            }
            case PER -> {
                effected.getStats().mergeMul(Stat.RUN_SPEED, (power / 100) + 1);
                effected.getStats().mergeMul(Stat.WALK_SPEED, (power / 100) + 1);
                effected.getStats().mergeMul(Stat.SWIM_RUN_SPEED, (power / 100) + 1);
                effected.getStats().mergeMul(Stat.SWIM_WALK_SPEED, (power / 100) + 1);
                effected.getStats().mergeMul(Stat.FLY_RUN_SPEED, (power / 100) + 1);
                effected.getStats().mergeMul(Stat.FLY_WALK_SPEED, (power / 100) + 1);
            }
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Speed(data);
        }

        @Override
        public String effectName() {
            return "speed";
        }
    }
}
