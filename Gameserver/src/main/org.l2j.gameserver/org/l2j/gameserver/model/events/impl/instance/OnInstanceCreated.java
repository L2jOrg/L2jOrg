package org.l2j.gameserver.model.events.impl.instance;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.instancezone.Instance;

/**
 * @author malyelfik
 */
public final class OnInstanceCreated implements IBaseEvent {
    private final Instance _instance;
    private final Player _creator;

    public OnInstanceCreated(Instance instance, Player creator) {
        _instance = instance;
        _creator = creator;
    }

    public Instance getInstanceWorld() {
        return _instance;
    }

    public Player getCreator() {
        return _creator;
    }

    @Override
    public EventType getType() {
        return EventType.ON_INSTANCE_CREATED;
    }
}