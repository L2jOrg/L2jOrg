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
package handlers.effecthandlers.stat;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
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
                effected.getStats().mergeMul(Stat.RUN_SPEED, power);
                effected.getStats().mergeMul(Stat.WALK_SPEED, power);
                effected.getStats().mergeMul(Stat.SWIM_RUN_SPEED, power);
                effected.getStats().mergeMul(Stat.SWIM_WALK_SPEED, power);
                effected.getStats().mergeMul(Stat.FLY_RUN_SPEED, power);
                effected.getStats().mergeMul(Stat.FLY_WALK_SPEED, power);
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
