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
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExMagicAttackInfo;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.*;

/**
 * HpCpHeal effect implementation.
 * @author Sdw
 * @author JoeAlisson
 *
 * TODO Extract super class with Heal
 */
public final class HpCpHeal extends AbstractEffect {
    private final double power;

    private HpCpHeal(StatsSet params)
    {
        power = params.getDouble("power", 0);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.HEAL;
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

        if (effected != effector && effected.isAffected(EffectFlag.FACEOFF)) {
            return;
        }

        double amount = calcHealAmount(effector, effected, skill);

        if(amount <= 0) {
            return;
        }

        var healAmount = Math.max(Math.min(amount, effected.getMaxRecoverableHp() - effected.getCurrentHp()), 0);
        if (healAmount != 0) {
            effected.setCurrentHp(healAmount + effected.getCurrentHp(), false);
            if(isPlayer(effected)) {
                sendMessage(effector, effected, (int) healAmount, SystemMessageId.S2_HP_HAS_BEEN_RESTORED_BY_C1, SystemMessageId.S1_HP_HAS_BEEN_RESTORED);
            }
        }

        if(isPlayer(effected) && healAmount < amount) {
            var cpAmount = Math.max(Math.min(amount - healAmount, effected.getMaxRecoverableCp() - effected.getCurrentCp()), 0);
            if(cpAmount > 0) {
                effected.setCurrentCp(cpAmount + effected.getCurrentCp(),false);
                sendMessage(effector, effected, (int) amount, SystemMessageId.S2_CP_HAS_BEEN_RESTORED_BY_C1, SystemMessageId.S1_CP_HAS_BEEN_RESTORED);
            }
        }
        effected.broadcastStatusUpdate(effector);
    }

    private void sendMessage(Creature effector, Creature effected, int healAmount, SystemMessageId msgRestoredByOther, SystemMessageId msgRestored) {
        if (isPlayer(effector) && (effector != effected)) {
            effected.sendPacket(getSystemMessage(msgRestoredByOther).addString(effector.getName()).addInt(healAmount));
        } else {
            effected.sendPacket(getSystemMessage(msgRestored).addInt(healAmount));
        }
    }

    private double calcHealAmount(Creature effector, Creature effected, Skill skill) {
        double amount = power;

        double staticShotBonus = 0;
        double mAtkMul = skill.isMagic() ? effector.chargedShotBonus(ShotType.SPIRITSHOTS) : 1;

        if (mAtkMul > 1 && (isPlayer(effector) && effector.getActingPlayer().isMageClass() || isSummon(effector))) {
            staticShotBonus = skill.getMpConsume();
            staticShotBonus *= mAtkMul >= 4 ? 2.4 : 1.0; // 2.4 if is blessed spiritshots TODO improve
        } else if (mAtkMul > 1  && isNpc(effector)) {
            staticShotBonus = 2.4 * skill.getMpConsume(); // always blessed spiritshots
        }
        else {
            // shot dynamic bonus
            mAtkMul = mAtkMul >= 4 ? mAtkMul * 4 : mAtkMul + 1;
        }

        if (!skill.isStatic()) {
            amount += staticShotBonus + Math.sqrt(mAtkMul * effector.getMAtk());
            amount *= effected.getStats().getValue(Stat.HEAL_RECEIVE, 1);
            amount = effector.getStats().getValue(Stat.HEAL_POWER, amount);
            // Heal critic, since CT2.3 Gracia Final
            if (skill.isMagic() && (Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill) || effector.isAffected(EffectFlag.HPCPHEAL_CRITICAL))) {
                amount *= 3;
                effector.sendPacket(SystemMessageId.M_CRITICAL);
                effector.sendPacket(new ExMagicAttackInfo(effector.getObjectId(), effected.getObjectId(), ExMagicAttackInfo.CRITICAL_HEAL));
                if (isPlayer(effected) && (effected != effector)) {
                    effected.sendPacket(new ExMagicAttackInfo(effector.getObjectId(), effected.getObjectId(), ExMagicAttackInfo.CRITICAL_HEAL));
                }
            }
        }
        return amount;
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new HpCpHeal(data);
        }

        @Override
        public String effectName() {
            return "HpCpHeal";
        }
    }
}
