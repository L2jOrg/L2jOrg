package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Duel;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.util.GameUtils;

public class SummonStatus extends PlayableStatus {
    public SummonStatus(L2Summon activeChar) {
        super(activeChar);
    }

    @Override
    public void reduceHp(double value, L2Character attacker) {
        reduceHp(value, attacker, true, false, false);
    }

    @Override
    public void reduceHp(double value, L2Character attacker, boolean awake, boolean isDOT, boolean isHPConsumption) {
        if ((attacker == null) || getActiveChar().isDead()) {
            return;
        }

        final L2PcInstance attackerPlayer = attacker.getActingPlayer();
        if ((attackerPlayer != null) && ((getActiveChar().getOwner() == null) || (getActiveChar().getOwner().getDuelId() != attackerPlayer.getDuelId()))) {
            attackerPlayer.setDuelState(Duel.DUELSTATE_INTERRUPTED);
        }

        final L2PcInstance caster = getActiveChar().getTransferingDamageTo();
        if (getActiveChar().getOwner().getParty() != null) {
            if ((caster != null) && GameUtils.checkIfInRange(1000, getActiveChar(), caster, true) && !caster.isDead() && getActiveChar().getParty().getMembers().contains(caster)) {
                int transferDmg = ((int) value * (int) getActiveChar().getStat().getValue(Stats.TRANSFER_DAMAGE_TO_PLAYER, 0)) / 100;
                transferDmg = Math.min((int) caster.getCurrentHp() - 1, transferDmg);
                if (transferDmg > 0) {
                    int membersInRange = 0;
                    for (L2PcInstance member : caster.getParty().getMembers()) {
                        if (GameUtils.checkIfInRange(1000, member, caster, false) && (member != caster)) {
                            membersInRange++;
                        }
                    }
                    if (attacker.isPlayable() && (caster.getCurrentCp() > 0)) {
                        if (caster.getCurrentCp() > transferDmg) {
                            caster.getStatus().reduceCp(transferDmg);
                        } else {
                            transferDmg = (int) (transferDmg - caster.getCurrentCp());
                            caster.getStatus().reduceCp((int) caster.getCurrentCp());
                        }
                    }
                    if (membersInRange > 0) {
                        caster.reduceCurrentHp(transferDmg / membersInRange, attacker, null);
                        value -= transferDmg;
                    }
                }
            }
        } else if ((caster != null) && (caster == getActiveChar().getOwner()) && GameUtils.checkIfInRange(1000, getActiveChar(), caster, true) && !caster.isDead()) // when no party, transfer only to owner (caster)
        {
            int transferDmg = ((int) value * (int) getActiveChar().getStat().getValue(Stats.TRANSFER_DAMAGE_TO_PLAYER, 0)) / 100;
            transferDmg = Math.min((int) caster.getCurrentHp() - 1, transferDmg);
            if (transferDmg > 0) {
                if (attacker.isPlayable() && (caster.getCurrentCp() > 0)) {
                    if (caster.getCurrentCp() > transferDmg) {
                        caster.getStatus().reduceCp(transferDmg);
                    } else {
                        transferDmg = (int) (transferDmg - caster.getCurrentCp());
                        caster.getStatus().reduceCp((int) caster.getCurrentCp());
                    }
                }

                caster.reduceCurrentHp(transferDmg, attacker, null);
                value -= transferDmg;
            }
        }
        super.reduceHp(value, attacker, awake, isDOT, isHPConsumption);
    }

    @Override
    public L2Summon getActiveChar() {
        return (L2Summon) super.getActiveChar();
    }
}
