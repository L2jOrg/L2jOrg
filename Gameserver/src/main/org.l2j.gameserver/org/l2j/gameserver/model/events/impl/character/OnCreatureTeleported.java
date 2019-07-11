package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnCreatureTeleported implements IBaseEvent {
    private final Creature _creature;

    public OnCreatureTeleported(Creature creature) {
        _creature = creature;
    }

    public Creature getCreature() {
        return _creature;
    }

    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_TELEPORTED;
    }
}
