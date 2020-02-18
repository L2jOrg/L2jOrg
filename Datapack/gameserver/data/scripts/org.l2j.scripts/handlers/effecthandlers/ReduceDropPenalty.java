package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.ReduceDropType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class ReduceDropPenalty extends AbstractEffect {
    private final double exp;
    private final double deathPenalty;
    private final ReduceDropType type;

    private ReduceDropPenalty(StatsSet params) {
        exp = params.getDouble("experience", 0);
        deathPenalty = params.getDouble("death-penalty", 0);
        type = params.getEnum("type", ReduceDropType.class, ReduceDropType.MOB);
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        switch (type) {
            case MOB -> reduce(effected, Stat.REDUCE_EXP_LOST_BY_MOB, Stat.REDUCE_DEATH_PENALTY_BY_MOB);
            case PK -> reduce(effected, Stat.REDUCE_EXP_LOST_BY_PVP, Stat.REDUCE_DEATH_PENALTY_BY_PVP);
            case RAID -> reduce(effected, Stat.REDUCE_EXP_LOST_BY_RAID, Stat.REDUCE_DEATH_PENALTY_BY_RAID);
            case ANY ->  {
                reduce(effected, Stat.REDUCE_EXP_LOST_BY_MOB, Stat.REDUCE_DEATH_PENALTY_BY_MOB);
                reduce(effected, Stat.REDUCE_EXP_LOST_BY_PVP, Stat.REDUCE_DEATH_PENALTY_BY_PVP);
                reduce(effected, Stat.REDUCE_EXP_LOST_BY_RAID, Stat.REDUCE_DEATH_PENALTY_BY_RAID);
            }
        }
    }

    private void reduce(Creature effected, Stat statExp, Stat statPenalty) {
        effected.getStats().mergeMul(statExp, (exp / 100) + 1);
        effected.getStats().mergeMul(statPenalty, (deathPenalty / 100) + 1);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new ReduceDropPenalty(data);
        }

        @Override
        public String effectName() {
            return "reduce-drop-penalty";
        }
    }
}
