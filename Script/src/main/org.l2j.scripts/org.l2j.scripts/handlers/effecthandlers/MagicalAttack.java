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
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.stats.Formulas;

import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Magical Attack effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class MagicalAttack extends AbstractEffect {
    private final double power;
    private final boolean overHit;

    private MagicalAttack(StatsSet params) {
        power = params.getDouble("power", 0);
        overHit = params.getBoolean("over-hit", false);
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
        if (effector.isAlikeDead()) {
            return;
        }

        if (isPlayer(effected) && effected.getActingPlayer().isFakeDeath()) {
            effected.stopFakeDeath(true);
        }

        if (overHit && isAttackable(effected)) {
            ((Attackable) effected).overhitEnabled(true);
        }

        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
        double damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power, effected.getMDef(), mcrit);

        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new MagicalAttack(data);
        }

        @Override
        public String effectName() {
            return "magical-attack";
        }
    }
}