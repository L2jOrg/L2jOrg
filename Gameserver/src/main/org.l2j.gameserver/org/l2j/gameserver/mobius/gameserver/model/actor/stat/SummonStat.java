package org.l2j.gameserver.mobius.gameserver.model.actor.stat;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;

public class SummonStat extends PlayableStat {
    public SummonStat(L2Summon activeChar) {
        super(activeChar);
    }

    @Override
    public L2Summon getActiveChar() {
        return (L2Summon) super.getActiveChar();
    }

    @Override
    public double getRunSpeed() {
        final double val = super.getRunSpeed() + Config.RUN_SPD_BOOST;

        // Apply max run speed cap.
        if (val > (Config.MAX_RUN_SPEED + 50)) // In retail maximum run speed is 350 for summons and 300 for players
        {
            return Config.MAX_RUN_SPEED + 50;
        }

        return val;
    }

    @Override
    public double getWalkSpeed() {
        final double val = super.getWalkSpeed() + Config.RUN_SPD_BOOST;

        // Apply max run speed cap.
        if (val > (Config.MAX_RUN_SPEED + 50)) // In retail maximum run speed is 350 for summons and 300 for players
        {
            return Config.MAX_RUN_SPEED + 50;
        }

        return val;
    }
}
