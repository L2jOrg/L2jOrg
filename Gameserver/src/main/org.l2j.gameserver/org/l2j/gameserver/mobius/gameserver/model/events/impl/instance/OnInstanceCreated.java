package org.l2j.gameserver.mobius.gameserver.model.events.impl.instance;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.mobius.gameserver.model.instancezone.Instance;

/**
 * @author malyelfik
 */
public final class OnInstanceCreated implements IBaseEvent {
    private final Instance _instance;
    private final L2PcInstance _creator;

    public OnInstanceCreated(Instance instance, L2PcInstance creator) {
        _instance = instance;
        _creator = creator;
    }

    public Instance getInstanceWorld() {
        return _instance;
    }

    public L2PcInstance getCreator() {
        return _creator;
    }

    @Override
    public EventType getType() {
        return EventType.ON_INSTANCE_CREATED;
    }
}