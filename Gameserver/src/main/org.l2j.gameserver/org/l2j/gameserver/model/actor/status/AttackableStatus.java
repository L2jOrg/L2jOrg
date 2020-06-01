package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;

public class AttackableStatus extends NpcStatus {
    public AttackableStatus(Attackable activeChar) {
        super(activeChar);
    }

    @Override
    public final void reduceHp(double value, Creature attacker) {
        reduceHp(value, attacker, true, false, false);
    }

    @Override
    public final void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHpConsumption) {
        if (getOwner().isDead()) {
            return;
        }

        if (value > 0) {
            if (getOwner().isOverhit()) {
                getOwner().setOverhitValues(attacker, value);
            } else {
                getOwner().overhitEnabled(false);
            }
        } else {
            getOwner().overhitEnabled(false);
        }

        super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);

        if (!getOwner().isDead()) {
            // And the attacker's hit didn't kill the mob, clear the over-hit flag
            getOwner().overhitEnabled(false);
        }
    }

    @Override
    public boolean setCurrentHp(double newHp, boolean broadcastPacket) {
        return super.setCurrentHp(newHp, true);
    }

    @Override
    public Attackable getOwner() {
        return (Attackable) super.getOwner();
    }
}