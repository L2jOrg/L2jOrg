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
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * MP change effect. It is mostly used for potions and static damage.
 * @author Nik
 * @author JoeAlisson
 */
public final class Mp extends AbstractVitalEffect {

    private Mp(StatsSet params) {
        super(params);
    }

    @Override
    protected boolean isVitalStatBlocked(Creature effected) {
        return effected.isMpBlocked();
    }

    @Override
    protected int maxVitalStat(Creature effected) {
        return effected.getMaxMp();
    }

    @Override
    protected SystemMessageId healMessage() {
        return SystemMessageId.S2_MP_HAS_BEEN_RESTORED_BY_C1;
    }

    @Override
    protected SystemMessageId selfHealingMessage() {
        return SystemMessageId.S1_MP_HAS_BEEN_RESTORED;
    }

    @Override
    protected void heal(Creature effector, Creature effected, Skill skill, double amount) {
        if(amount > 0) {
            effected.setCurrentMp(amount + effected.getCurrentMp(), false);
        } else {
            effected.reduceCurrentMp(-amount);
        }
    }

    @Override
    protected double maxHealAllowed(Creature effected) {
        return effected.getMaxRecoverableMp() - effected.getCurrentMp();
    }

    @Override
    protected Stat additionalStat() {
        return Stat.ADDITIONAL_POTION_MP;
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Mp(data);
        }

        @Override
        public String effectName() {
            return "mp";
        }
    }
}
