package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.actor.Creature;

/**
 * @author Sdw
 */
public class WarpedSpaceHolder {
    private final Creature _creature;
    private final int _range;

    public WarpedSpaceHolder(Creature creature, int range) {
        _creature = creature;
        _range = range;
    }

    public Creature getCreature() {
        return _creature;
    }

    public int getRange() {
        return _range;
    }
}