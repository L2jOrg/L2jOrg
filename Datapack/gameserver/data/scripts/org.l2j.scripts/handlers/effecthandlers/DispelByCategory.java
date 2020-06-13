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
import org.l2j.gameserver.enums.DispelSlotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.stats.Formulas;

import java.util.List;

/**
 * Dispel By Category effect implementation.
 * @author DS, Adry_85
 * @author JoeAlisson
 */
public final class DispelByCategory extends AbstractEffect {
    private final DispelSlotType category;
    private final int power;
    private final int max;

    private DispelByCategory(StatsSet params) {
        category = params.getEnum("category", DispelSlotType.class, DispelSlotType.BUFF);
        power = params.getInt("power", 0);
        max = params.getInt("max", 0);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.DISPEL;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected.isDead()) {
            return;
        }

        final List<BuffInfo> canceled = Formulas.calcCancelStealEffects(effector, effected, skill, category, power, max);
        canceled.forEach(b -> effected.getEffectList().stopSkillEffects(true, b.getSkill()));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new DispelByCategory(data);
        }

        @Override
        public String effectName() {
            return "dispel-by-category";
        }
    }
}