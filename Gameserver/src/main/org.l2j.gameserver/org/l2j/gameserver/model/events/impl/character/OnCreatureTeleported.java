package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnCreatureTeleported implements IBaseEvent {
    private final L2Character _creature;

    public OnCreatureTeleported(L2Character creature) {
        _creature = creature;
    }

    public L2Character getCreature() {
        return _creature;
    }

    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_TELEPORTED;
    }
}
