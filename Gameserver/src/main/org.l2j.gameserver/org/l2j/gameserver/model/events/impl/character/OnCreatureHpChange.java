package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnCreatureHpChange implements IBaseEvent {
    private final Creature _creature;
    private final double _newHp;
    private final double _oldHp;

    public OnCreatureHpChange(Creature creature, double oldHp, double newHp) {
        _creature = creature;
        _oldHp = oldHp;
        _newHp = newHp;
    }

    public Creature getCreature() {
        return _creature;
    }

    public double getOldHp() {
        return _oldHp;
    }

    public double getNewHp() {
        return _newHp;
    }

    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_HP_CHANGE;
    }
}
