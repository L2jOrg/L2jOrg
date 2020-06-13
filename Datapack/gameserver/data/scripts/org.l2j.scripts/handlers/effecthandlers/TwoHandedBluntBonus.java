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
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.conditions.ConditionUsingItemType;
import org.l2j.gameserver.model.conditions.ConditionUsingSlotType;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class TwoHandedBluntBonus extends AbstractEffect {

    private static final Condition weaponTypeCondition = new ConditionUsingItemType(WeaponType.BLUNT.mask());
    private static final Condition slotCondition = new ConditionUsingSlotType(BodyPart.TWO_HAND.getId());

    private final double pAtkAmount;
    private final StatModifierType pAtkmode;

    private final double accuracyAmount;
    private final StatModifierType accuracyMode;

    private TwoHandedBluntBonus(StatsSet params) {
        pAtkAmount = params.getDouble("power", 0);
        pAtkmode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);

        accuracyAmount = params.getDouble("accuracy", 0);
        accuracyMode = params.getEnum("accuracy-mode", StatModifierType.class, StatModifierType.DIFF);
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        if (weaponTypeCondition.test(effected, effected, skill) && slotCondition.test(effected, effected, skill)) {
            switch (pAtkmode) {
                case DIFF -> effected.getStats().mergeAdd(Stat.PHYSICAL_ATTACK, pAtkAmount);
                case PER -> effected.getStats().mergeMul(Stat.PHYSICAL_ATTACK, pAtkAmount);
            }

            switch (accuracyMode) {
                case DIFF -> effected.getStats().mergeAdd(Stat.ACCURACY, accuracyAmount);
                case PER -> effected.getStats().mergeMul(Stat.ACCURACY, accuracyAmount);
            }
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TwoHandedBluntBonus(data);
        }

        @Override
        public String effectName() {
            return "two-hand-blunt-bonus";
        }
    }
}
