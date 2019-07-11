package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Duel;

public class NpcStatus extends CharStatus {
    public NpcStatus(L2Npc activeChar) {
        super(activeChar);
    }

    @Override
    public void reduceHp(double value, L2Character attacker) {
        reduceHp(value, attacker, true, false, false);
    }

    @Override
    public void reduceHp(double value, L2Character attacker, boolean awake, boolean isDOT, boolean isHpConsumption) {
        if (getActiveChar().isDead()) {
            return;
        }

        if (attacker != null) {
            final Player attackerPlayer = attacker.getActingPlayer();
            if ((attackerPlayer != null) && attackerPlayer.isInDuel()) {
                attackerPlayer.setDuelState(Duel.DUELSTATE_INTERRUPTED);
            }

            // Add attackers to npc's attacker list
            getActiveChar().addAttackerToAttackByList(attacker);
        }

        super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);
    }

    @Override
    public L2Npc getActiveChar() {
        return (L2Npc) super.getActiveChar();
    }
}
