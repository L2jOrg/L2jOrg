package handlers.effecthandlers;

import org.l2j.gameserver.enums.ReduceDropType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stats;

/**
 * @author Sdw
 */
public class ReduceDropPenalty extends AbstractEffect
{
    private final double _exp;
    private final double _deathPenalty;
    private final ReduceDropType _type;

    public ReduceDropPenalty(StatsSet params)
    {
        _exp = params.getDouble("exp", 0);
        _deathPenalty = params.getDouble("deathPenalty", 0);
        _type = params.getEnum("type", ReduceDropType.class, ReduceDropType.MOB);
    }

    @Override
    public void pump(L2Character effected, Skill skill) {
        switch (_type) {
            case MOB -> reduce(effected, Stats.REDUCE_EXP_LOST_BY_MOB, Stats.REDUCE_DEATH_PENALTY_BY_MOB);
            case PK -> reduce(effected, Stats.REDUCE_EXP_LOST_BY_PVP, Stats.REDUCE_DEATH_PENALTY_BY_PVP);
            case RAID -> reduce(effected, Stats.REDUCE_EXP_LOST_BY_RAID, Stats.REDUCE_DEATH_PENALTY_BY_RAID);
            case ANY ->  {
                reduce(effected, Stats.REDUCE_EXP_LOST_BY_MOB, Stats.REDUCE_DEATH_PENALTY_BY_MOB);
                reduce(effected, Stats.REDUCE_EXP_LOST_BY_PVP, Stats.REDUCE_DEATH_PENALTY_BY_PVP);
                reduce(effected, Stats.REDUCE_EXP_LOST_BY_RAID, Stats.REDUCE_DEATH_PENALTY_BY_RAID);
            }
        }
    }

    private void reduce(L2Character effected, Stats statExp, Stats statPenalty) {
        effected.getStat().mergeMul(statExp, (_exp / 100) + 1);
        effected.getStat().mergeMul(statPenalty, (_deathPenalty / 100) + 1);
    }
}
