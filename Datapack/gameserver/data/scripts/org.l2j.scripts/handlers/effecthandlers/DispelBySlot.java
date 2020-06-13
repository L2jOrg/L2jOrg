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
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

/**
 * Dispel By Slot effect implementation.
 * @author Gnacik, Zoey76, Adry_85
 * @author JoeAlisson
 */
public final class DispelBySlot extends AbstractEffect {

    private final Map<AbnormalType, Short> dispelAbnormals;

    private DispelBySlot(StatsSet params) {
        if(params.contains("type")) {
            dispelAbnormals = Map.of(params.getEnum("type", AbnormalType.class), params.getShort("power"));
        } else {
            dispelAbnormals = new HashMap<>();
            params.getSet().forEach((key, value) -> {
                if(key.startsWith("abnormal")) {
                    var set = (StatsSet) value;
                    dispelAbnormals.put(set.getEnum("type", AbnormalType.class), set.getShort("power"));
                }
            });
        }
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.DISPEL_BY_SLOT;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (dispelAbnormals.isEmpty()) {
            return;
        }

        // Continue only if target has any of the abnormals. Save useless cycles.
        if (effected.getEffectList().hasAbnormalType(dispelAbnormals.keySet())) {
            // Dispel transformations (buff and by GM)
            final Short transformToDispel = dispelAbnormals.get(AbnormalType.TRANSFORM);
            if (nonNull(transformToDispel) && ((transformToDispel == effected.getTransformationId()) || (transformToDispel < 0))) {
                effected.stopTransformation(true);
            }

            effected.getEffectList().stopEffects(info -> {
                // We have already dealt with transformation from above.
                if (info.isAbnormalType(AbnormalType.TRANSFORM)) {
                    return false;
                }

                final Short abnormalLevel = dispelAbnormals.get(info.getSkill().getAbnormalType());
                return (abnormalLevel != null) && ((abnormalLevel < 0) || (abnormalLevel >= info.getSkill().getAbnormalLvl()));
            }, true, true);
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new DispelBySlot(data);
        }

        @Override
        public String effectName() {
            return "dispel";
        }
    }
}
