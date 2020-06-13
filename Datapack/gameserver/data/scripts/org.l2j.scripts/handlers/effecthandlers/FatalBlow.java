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
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * Fatal Blow effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class FatalBlow extends AbstractEffect {
    private final double power;
    private final double chanceBoost;
    private final double criticalChance;
    private final boolean overHit;

    private FatalBlow(StatsSet params) {
        power = params.getDouble("power");
        chanceBoost = params.getDouble("chance-boost");
        criticalChance = params.getDouble("critical-chance", 0);
        overHit = params.getBoolean("over-hit", false);
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
    {
        return !Formulas.calcPhysicalSkillEvasion(effector, effected, skill) && Formulas.calcBlowSuccess(effector, effected, skill, chanceBoost);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.PHYSICAL_ATTACK;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effector.isAlikeDead()) {
            return;
        }

        if (overHit && isAttackable(effected)) {
            ((Attackable) effected).overhitEnabled(true);
        }

        double damage = Formulas.calcBlowDamage(effector, effected, skill, power);
        final boolean crit = Formulas.calcCrit(criticalChance, effector, effected, skill);

        if (crit) {
            damage *= 2;
        }

        effector.doAttack(damage, effected, skill, false, false, true, false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new FatalBlow(data);
        }

        @Override
        public String effectName() {
            return "fatal-blow";
        }
    }
}