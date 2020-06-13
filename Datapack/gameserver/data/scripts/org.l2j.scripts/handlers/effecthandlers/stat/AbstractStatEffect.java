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
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.conditions.ConditionUsingItemType;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.stats.Stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.l2j.commons.util.Util.SPACE;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public abstract class AbstractStatEffect extends AbstractEffect {

    public final Stat addStat;
    public final Stat mulStat;
    public final double power;
    public final StatModifierType mode;
    public final List<Condition> conditions = new ArrayList<>();

    public AbstractStatEffect(StatsSet params, Stat stat) {
        this(params, stat, stat);
    }

    public AbstractStatEffect(StatsSet params, Stat mulStat, Stat addStat) {
        this.addStat = addStat;
        this.mulStat = mulStat;
        power = params.getDouble("power", 0);
        mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);

        if(params.contains("weapon-type")) {
            int weaponTypesMask = Arrays.stream(params.getString("weapon-type").trim().split(SPACE)).mapToInt(w -> WeaponType.valueOf(w).mask()).reduce(0, (a, b) -> a | b);
            conditions.add(new ConditionUsingItemType(weaponTypesMask));
        }

        if(params.contains("armor-type")) {
            int armorTypesMask = Arrays.stream(params.getString("armor-type").trim().split(SPACE)).mapToInt(armor -> ArmorType.valueOf(armor).mask()).reduce(0, (a, b) -> a | b);
            conditions.add(new ConditionUsingItemType(armorTypesMask));
        }
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        if (conditions.isEmpty() || conditions.stream().allMatch(cond -> cond.test(effected, effected, skill))) {
            switch (mode) {
                case DIFF -> effected.getStats().mergeAdd(addStat, power);
                case PER -> effected.getStats().mergeMul(mulStat, power);
            }
        }
    }
}
