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
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.TraitType;

import java.util.HashMap;
import java.util.Map;

/**
 * Defence Trait effect implementation.
 * @author NosBit
 * @author JoeAlisson
 */
public final class DefenceTrait extends AbstractEffect {
    private final Map<TraitType, Float> defenceTraits;

    private DefenceTrait(StatsSet params) {
        if(params.contains("type")) {
            defenceTraits = Map.of(params.getEnum("type", TraitType.class), params.getFloat("power") / 100);
        } else {
            defenceTraits = new HashMap<>();
            params.getSet().forEach((key, value) -> {
                if(key.startsWith("trait")) {
                    var set = (StatsSet) value;
                    defenceTraits.put(set.getEnum("type", TraitType.class), set.getFloat("power") / 100);
                }
            });
        }
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        for (var trait : defenceTraits.entrySet()) {
            if (trait.getValue() < 1.0f) {
                effected.getStats().mergeDefenceTrait(trait.getKey(), trait.getValue());
            } else {
                effected.getStats().mergeInvulnerableTrait(trait.getKey());
            }
        }
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        for (var trait : defenceTraits.entrySet()) {
            if (trait.getValue() < 1.0f) {
                effected.getStats().removeDefenceTrait(trait.getKey(), trait.getValue());
            } else {
                effected.getStats().removeInvulnerableTrait(trait.getKey());
            }
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new DefenceTrait(data);
        }

        @Override
        public String effectName() {
            return "defence-trait";
        }
    }
}
