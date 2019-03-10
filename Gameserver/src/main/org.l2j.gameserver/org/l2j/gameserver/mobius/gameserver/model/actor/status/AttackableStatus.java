package org.l2j.gameserver.mobius.gameserver.model.actor.status;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;

public class AttackableStatus extends NpcStatus {
    public AttackableStatus(L2Attackable activeChar) {
        super(activeChar);
    }

    @Override
    public final void reduceHp(double value, L2Character attacker) {
        reduceHp(value, attacker, true, false, false);
    }

    @Override
    public final void reduceHp(double value, L2Character attacker, boolean awake, boolean isDOT, boolean isHpConsumption) {
        if (getActiveChar().isDead()) {
            return;
        }

        if (value > 0) {
            if (getActiveChar().isOverhit()) {
                getActiveChar().setOverhitValues(attacker, value);
            } else {
                getActiveChar().overhitEnabled(false);
            }
        } else {
            getActiveChar().overhitEnabled(false);
        }

        super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);

        if (!getActiveChar().isDead()) {
            // And the attacker's hit didn't kill the mob, clear the over-hit flag
            getActiveChar().overhitEnabled(false);
        }
    }

    @Override
    public boolean setCurrentHp(double newHp, boolean broadcastPacket) {
        return super.setCurrentHp(newHp, true);
    }

    @Override
    public L2Attackable getActiveChar() {
        return (L2Attackable) super.getActiveChar();
    }
}