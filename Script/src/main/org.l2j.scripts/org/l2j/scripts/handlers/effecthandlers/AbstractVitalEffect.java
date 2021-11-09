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
import org.l2j.gameserver.enums.StatModifierType;
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
 * @author JoeAlisson
 */
abstract class AbstractVitalEffect extends AbstractEffect {

    private final int power;
    private final StatModifierType mode;

    protected  AbstractVitalEffect(StatsSet params) {
        power = params.getInt("power", 0);
        mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected.isDead() || isDoor(effected) || isVitalStatBlocked(effected)) {
            return;
        }

        double basicAmount = power;
        if ( nonNull(item) && (item.isPotion() || item.isElixir())) {
            basicAmount += effected.getStats().getValue(additionalStat(), 0);
        }

        double amount = switch (mode) {
            case DIFF -> Math.min(basicAmount, maxHealAllowed(effected));
            case PER -> Math.min((maxVitalStat(effected) * basicAmount) / 100.0, maxHealAllowed(effected));
        };

        if(amount == 0) {
            return;
        }

        heal(effector, effected, skill, amount);
        effected.broadcastStatusUpdate(effector);

        if(amount > 0) {
            effected.sendPacket(healingMessage(effector, effected, (int) amount));
        } else {
            effector.sendDamageMessage(effected, skill, (int) -amount, 0, false, false);
        }
    }

    protected abstract boolean isVitalStatBlocked(Creature effected);

    private SystemMessage healingMessage(Creature effector, Creature effected, int amount) {
        SystemMessage sm;
        if (effector.getObjectId() != effected.getObjectId()) {
            sm = getSystemMessage(healMessage()).addString(effector.getName());
        } else {
            sm = getSystemMessage(selfHealingMessage());
        }
        sm.addInt(amount);
        return sm;
    }

    protected abstract int maxVitalStat(Creature effected);

    protected abstract SystemMessageId healMessage();

    protected abstract SystemMessageId selfHealingMessage();

    protected abstract void heal(Creature effector, Creature effected, Skill skill, double amount);

    protected abstract double maxHealAllowed(Creature effected);

    protected abstract Stat additionalStat();
}
