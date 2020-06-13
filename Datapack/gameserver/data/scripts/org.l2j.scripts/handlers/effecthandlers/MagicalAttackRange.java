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
import org.l2j.gameserver.model.stats.Formulas;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Magical Attack effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class MagicalAttackRange extends AbstractEffect {
    private final double power;
    private final double shieldDefPercent;

    private MagicalAttackRange(StatsSet params) {
        power = params.getDouble("power");
        shieldDefPercent = params.getDouble("shield-defense", 0);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.MAGICAL_ATTACK;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayer(effected) && effected.getActingPlayer().isFakeDeath()) {
            effected.stopFakeDeath(true);
        }

        double mDef = effected.getMDef();
        switch (Formulas.calcShldUse(effector, effected)) {
            case Formulas.SHIELD_DEFENSE_SUCCEED -> mDef += ((effected.getShldDef() * shieldDefPercent) / 100);
            case Formulas.SHIELD_DEFENSE_PERFECT_BLOCK -> mDef = -1;
        }

        double damage = 1;
        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);

        if (mDef != -1) {
            damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power, mDef, mcrit);
        }

        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new MagicalAttackRange(data);
        }

        @Override
        public String effectName() {
            return "magical-attack-range";
        }
    }
}