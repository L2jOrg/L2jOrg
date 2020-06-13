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
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.*;

/**
 * Lethal effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class Lethal extends AbstractEffect {
    private final double fullLethal;
    private final double halfLethal;

    private Lethal(StatsSet params) {
        fullLethal = params.getDouble("power", 0);
        halfLethal = params.getDouble("half-power", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.LETHAL_ATTACK;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayer(effector) && !effector.getAccessLevel().canGiveDamage()) {
            return;
        }

        if (skill.getMagicLevel() < (effected.getLevel() - 6)) {
            return;
        }

        if (!effected.isLethalable() || effected.isHpBlocked()) {
            return;
        }

        if (isPlayer(effector) && isPlayer(effected) && effected.isAffected(EffectFlag.DUELIST_FURY) && !effector.isAffected(EffectFlag.DUELIST_FURY)) {
            return;
        }

        final double chanceMultiplier = Formulas.calcAttributeBonus(effector, effected, skill) * Formulas.calcGeneralTraitBonus(effector, effected, skill.getTrait(), false);

        // Calculate instant kill resistance first.
        if (Rnd.get(100) < effected.getStats().getValue(Stat.INSTANT_KILL_RESIST, 0)) {
            effected.sendPacket(getSystemMessage(SystemMessageId.C1_HAS_EVADED_C2_S_ATTACK).addString(effected.getName()).addString(effector.getName()));
            effector.sendPacket(getSystemMessage(SystemMessageId.C1_S_ATTACK_WENT_ASTRAY).addString(effector.getName()));
        }
        // Lethal Strike
        else if (Rnd.get(100) < fullLethal * chanceMultiplier) {
            // for Players CP and HP is set to 1.
            if (isPlayer(effected)) {
                effected.setCurrentCp(1);
                effected.setCurrentHp(1);
                effected.sendPacket(SystemMessageId.LETHAL_STRIKE);
            }
            // for Monsters HP is set to 1.
            else if (isMonster(effected) || isSummon(effected)) {
                effected.setCurrentHp(1);
            }
            effector.sendPacket(SystemMessageId.HIT_WITH_LETHAL_STRIKE);
        }
        // Half-Kill
        else if (Rnd.get(100) < (halfLethal * chanceMultiplier)) {
            // for Players CP is set to 1.
            if (isPlayer(effected)) {
                effected.setCurrentCp(1);
                effected.sendPacket(SystemMessageId.HALF_KILL);
                effected.sendPacket(SystemMessageId.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_HALF_KILL_SKILL);
            }
            // for Monsters HP is set to 50%.
            else if (isMonster(effected) || isSummon(effected)) {
                effected.setCurrentHp(effected.getCurrentHp() * 0.5);
            }
            effector.sendPacket(SystemMessageId.HALF_KILL);
        }

        // No matter if lethal succeeded or not, its reflected.
        Formulas.calcCounterAttack(effector, effected, skill, false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Lethal(data);
        }

        @Override
        public String effectName() {
            return "lethal";
        }
    }
}
