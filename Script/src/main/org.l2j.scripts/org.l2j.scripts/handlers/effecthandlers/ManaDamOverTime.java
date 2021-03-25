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
package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Mana Damage Over Time effect implementation.
 * @author JoeAlisson
 */
public final class ManaDamOverTime extends AbstractEffect {

    private final double power;

    private ManaDamOverTime(StatsSet params) {
        power = params.getDouble("power", 0);
        setTicks(params.getInt("ticks"));
    }

    @Override
    public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected.isDead()) {
            return false;
        }

        final double manaDam = power * getTicksMultiplier();
        if (manaDam > effected.getCurrentMp() && skill.isToggle()) {
            effected.sendPacket(SystemMessageId.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
            return false;
        }

        effected.reduceCurrentMp(manaDam);
        return skill.isToggle();
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new ManaDamOverTime(data);
        }

        @Override
        public String effectName() {
            return "ManaDamOverTime";
        }
    }
}
