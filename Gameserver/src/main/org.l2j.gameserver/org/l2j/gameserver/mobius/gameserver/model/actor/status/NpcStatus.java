package org.l2j.gameserver.mobius.gameserver.model.actor.status;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.entity.Duel;

public class NpcStatus extends CharStatus
{
    public NpcStatus(L2Npc activeChar)
    {
        super(activeChar);
    }

    @Override
    public void reduceHp(double value, L2Character attacker)
    {
        reduceHp(value, attacker, true, false, false);
    }

    @Override
    public void reduceHp(double value, L2Character attacker, boolean awake, boolean isDOT, boolean isHpConsumption)
    {
        if (getActiveChar().isDead())
        {
            return;
        }

        if (attacker != null)
        {
            final L2PcInstance attackerPlayer = attacker.getActingPlayer();
            if ((attackerPlayer != null) && attackerPlayer.isInDuel())
            {
                attackerPlayer.setDuelState(Duel.DUELSTATE_INTERRUPTED);
            }

            // Add attackers to npc's attacker list
            getActiveChar().addAttackerToAttackByList(attacker);
        }

        super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);
    }

    @Override
    public L2Npc getActiveChar()
    {
        return (L2Npc) super.getActiveChar();
    }
}
