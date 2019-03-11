package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.actor.L2Character;

/**
 * @author Sdw
 */
public class WarpedSpaceHolder {
    private final L2Character _creature;
    private final int _range;

    public WarpedSpaceHolder(L2Character creature, int range) {
        _creature = creature;
        _range = range;
    }

    public L2Character getCreature() {
        return _creature;
    }

    public int getRange() {
        return _range;
    }
}