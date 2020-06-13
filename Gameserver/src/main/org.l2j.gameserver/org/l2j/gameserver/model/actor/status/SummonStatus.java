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
package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Duel;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.util.GameUtils;

import static org.l2j.gameserver.model.DamageInfo.DamageType.TRANSFERED_DAMAGE;
import static org.l2j.gameserver.util.GameUtils.isPlayable;

public class SummonStatus extends PlayableStatus {
    public SummonStatus(Summon activeChar) {
        super(activeChar);
    }

    @Override
    public void reduceHp(double value, Creature attacker) {
        reduceHp(value, attacker, true, false, false);
    }

    @Override
    public void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHPConsumption) {
        if ((attacker == null) || getOwner().isDead()) {
            return;
        }

        final Player attackerPlayer = attacker.getActingPlayer();
        if ((attackerPlayer != null) && ((getOwner().getOwner() == null) || (getOwner().getOwner().getDuelId() != attackerPlayer.getDuelId()))) {
            attackerPlayer.setDuelState(Duel.DUELSTATE_INTERRUPTED);
        }

        final Player caster = getOwner().getTransferingDamageTo();
        if (getOwner().getOwner().getParty() != null) {
            if ((caster != null) && GameUtils.checkIfInRange(1000, getOwner(), caster, true) && !caster.isDead() && getOwner().getParty().getMembers().contains(caster)) {
                double transferDmg = value * getOwner().getStats().getValue(Stat.TRANSFER_DAMAGE_TO_PLAYER, 0) / 100;
                transferDmg = Math.min((int) caster.getCurrentHp() - 1, transferDmg);
                if (transferDmg > 0) {
                    int membersInRange = 0;
                    for (Player member : caster.getParty().getMembers()) {
                        if (GameUtils.checkIfInRange(1000, member, caster, false) && (member != caster)) {
                            membersInRange++;
                        }
                    }
                    if (membersInRange > 0) {
                        caster.reduceCurrentHp(transferDmg / membersInRange, attacker, null, false, false, false, false, TRANSFERED_DAMAGE);
                        value -= transferDmg;
                    }
                }
            }
        } else if ((caster != null) && (caster == getOwner().getOwner()) && GameUtils.checkIfInRange(1000, getOwner(), caster, true) && !caster.isDead()) // when no party, transfer only to owner (caster)
        {
            int transferDmg = ((int) value * (int) getOwner().getStats().getValue(Stat.TRANSFER_DAMAGE_TO_PLAYER, 0)) / 100;
            transferDmg = Math.min((int) caster.getCurrentHp() - 1, transferDmg);
            if (transferDmg > 0) {
                if (isPlayable(attacker) && (caster.getCurrentCp() > 0)) {
                    if (caster.getCurrentCp() > transferDmg) {
                        caster.getStatus().reduceCp(transferDmg);
                    } else {
                        transferDmg = (int) (transferDmg - caster.getCurrentCp());
                        caster.getStatus().reduceCp((int) caster.getCurrentCp());
                    }
                }

                caster.reduceCurrentHp(transferDmg, attacker, null, TRANSFERED_DAMAGE);
                value -= transferDmg;
            }
        }
        super.reduceHp(value, attacker, awake, isDOT, isHPConsumption);
    }

    @Override
    public Summon getOwner() {
        return (Summon) super.getOwner();
    }
}
