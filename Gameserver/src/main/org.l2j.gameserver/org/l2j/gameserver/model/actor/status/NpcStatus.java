package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Duel;

public class NpcStatus extends CharStatus {
    public NpcStatus(Npc activeChar) {
        super(activeChar);
    }

    @Override
    public void reduceHp(double value, Creature attacker) {
        reduceHp(value, attacker, true, false, false);
    }

    @Override
    public void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHpConsumption) {
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
    public Npc getActiveChar() {
        return (Npc) super.getActiveChar();
    }
}
