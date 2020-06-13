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
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Mana Heal effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class ManaHeal extends AbstractEffect {
    private final double power;

    private ManaHeal(StatsSet params)
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
        if (effected.isDead() || isDoor(effected) || effected.isMpBlocked()) {
            return;
        }

        if (effected != effector && effected.isAffected(EffectFlag.FACEOFF)) {
            return;
        }

        double amount = power;
        if ((item != null) && (item.isPotion() || item.isElixir())) {
            amount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_MP, 0);
        }

        if (!skill.isStatic()) {
            amount = effected.getStats().getValue(Stat.MANA_CHARGE, amount);
        }

        // Prevents overheal and negative amount
        amount = Math.max(Math.min(amount, effected.getMaxRecoverableMp() - effected.getCurrentMp()), 0);
        if (amount != 0) {
            effected.setCurrentMp(effected.getCurrentMp() + amount);
            effected.broadcastStatusUpdate(effector);
        }

        SystemMessage sm;
        if (effector.getObjectId() != effected.getObjectId()) {
            sm = getSystemMessage(SystemMessageId.S2_MP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName());
        } else {
            sm = getSystemMessage(SystemMessageId.S1_MP_HAS_BEEN_RESTORED);
        }
        effected.sendPacket(sm.addInt((int) amount));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new ManaHeal(data);
        }

        @Override
        public String effectName() {
            return "ManaHeal";
        }
    }
}
