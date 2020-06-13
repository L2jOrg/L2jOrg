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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Unsummon effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class Unsummon extends AbstractEffect {
    private final int power;

    private Unsummon(StatsSet params)
    {
        power = params.getInt("power", -1);
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill) {
        if (power < 0) {
            return true;
        }

        final int magicLevel = skill.getMagicLevel();
        if ((magicLevel <= 0) || ((effected.getLevel() - 9) <= magicLevel))
        {
            final double chance = this.power * Formulas.calcAttributeBonus(effector, effected, skill) * Formulas.calcGeneralTraitBonus(effector, effected, skill.getTrait(), false);
            return Rnd.chance(chance);
        }

        return false;
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {
        return isSummon(effected);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected.isServitor()) {
            final Summon servitor = (Summon) effected;
            final Player summonOwner = servitor.getOwner();

            servitor.abortAttack();
            servitor.abortCast();
            servitor.stopAllEffects();

            servitor.unSummon(summonOwner);
            summonOwner.sendPacket(SystemMessageId.YOUR_SERVITOR_HAS_VANISHED_YOU_LL_NEED_TO_SUMMON_A_NEW_ONE);
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Unsummon(data);
        }

        @Override
        public String effectName() {
            return "Unsummon";
        }
    }
}
