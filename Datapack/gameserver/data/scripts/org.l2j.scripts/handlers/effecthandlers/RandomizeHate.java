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
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.world.World;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * Randomize Hate effect implementation.
 * @author JoeAlisson
 */
public final class RandomizeHate extends AbstractEffect {
    private final double power;

    private RandomizeHate(StatsSet params) {
        power = params.getDouble("power", 100);
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
    {
        return Formulas.calcProbability(power, effector, effected, skill);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected == effector || !isAttackable(effected)) {
            return;
        }

        final Attackable effectedMob = (Attackable) effected;

        var target = World.getInstance().findAnyVisibleObject(effected, Creature.class, getSettings(CharacterSettings.class).partyRange() / 2, false,
                creature -> creature != effector && (!isAttackable(creature) || !((Attackable)creature).isInMyClan(effectedMob)));

        final int hate = effectedMob.getHating(effector);
        effectedMob.stopHating(effector);
        effectedMob.addDamageHate(target, 0, hate);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new RandomizeHate(data);
        }

        @Override
        public String effectName() {
            return "RandomizeHate";
        }
    }
}