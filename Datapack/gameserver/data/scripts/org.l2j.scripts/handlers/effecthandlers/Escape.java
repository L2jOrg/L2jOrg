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
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;

import static java.util.Objects.nonNull;

/**
 * Escape effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class Escape extends AbstractEffect {
    private final TeleportWhereType location;

    private Escape(StatsSet params) {
        location = params.getEnum("location", TeleportWhereType.class, null);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.TELEPORT;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill) {
        // While affected by escape blocking effect you cannot use Blink or Scroll of Escape
        return super.canStart(effector, effected, skill) && !effected.cannotEscape();
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (nonNull(location)) {
            effected.teleToLocation(location, null);
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Escape(data);
        }

        @Override
        public String effectName() {
            return "escape";
        }
    }
}
