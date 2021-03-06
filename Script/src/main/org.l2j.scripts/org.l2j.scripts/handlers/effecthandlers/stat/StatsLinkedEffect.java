/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.handlers.effecthandlers.stat;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author JoeAlisson
 */
public class StatsLinkedEffect extends AbstractEffect {

    public final double power;
    private final Stat stat;
    private final Stat baseStat;

    public StatsLinkedEffect(StatsSet data) {
        power = data.getDouble("power", 0);
        stat = data.getEnum("stat", Stat.class);
        baseStat = data.getEnum("base-stat", Stat.class);
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        var base = effected.getStats().getValue(baseStat);
        effected.getStats().mergeAdd(stat, base * power / 100);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new StatsLinkedEffect(data);
        }

        @Override
        public String effectName() {
            return "stats-linked";
        }
    }
}
