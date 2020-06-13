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
import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author JoeAlisson
 */
public class StatPositionBased extends AbstractEffect {

    private final double power;
    private final Position position;
    private final Stat stat;

    private StatPositionBased(StatsSet params) {
        stat = params.getEnum("stat", Stat.class);
        power = params.getDouble("power", 0);
        position = params.getEnum("position", Position.class, Position.FRONT);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item)
    {
        effected.getStats().mergePositionTypeValue(stat, position, (power / 100) + 1, MathUtil::mul);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.getStats().mergePositionTypeValue(stat, position, (power / 100) + 1, MathUtil::div);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new StatPositionBased(data);
        }

        @Override
        public String effectName() {
            return "stat-position-based";
        }
    }
}
