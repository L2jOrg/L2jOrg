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

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Cp Heal Percent effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class CpHealPercent extends AbstractEffect {
    private final double power;

    private CpHealPercent(StatsSet params)
    {
        power = params.getDouble("power", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected.isDead() || isDoor(effected) || effected.isHpBlocked()) {
            return;
        }

        double amount;
        final boolean full = power == 100;

        amount = full ? effected.getMaxCp() : (effected.getMaxCp() * power) / 100.0;
        if (nonNull(item) && (item.isPotion() || item.isElixir()))
        {
            amount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_CP, 0);
        }
        // Prevents overheal and negative amount
        amount = Math.max(Math.min(amount, effected.getMaxRecoverableCp() - effected.getCurrentCp()), 0);
        if (amount != 0) {
            effected.setCurrentCp(amount + effected.getCurrentCp(), false);
            effected.broadcastStatusUpdate(effector);
        }

        SystemMessage sm;
        if (nonNull(effector) && (effector != effected)) {
            sm = getSystemMessage(SystemMessageId.S2_CP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName());
        } else {
            sm = getSystemMessage(SystemMessageId.S1_CP_HAS_BEEN_RESTORED);
        }
        effected.sendPacket(sm.addInt((int) amount));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new CpHealPercent(data);
        }

        @Override
        public String effectName() {
            return "CpHealPercent";
        }
    }
}
