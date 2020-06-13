/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.ReduceDropType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
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
        effected.getStats().mergeMul(statExp, exp);
        effected.getStats().mergeMul(statPenalty, deathPenalty);
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
