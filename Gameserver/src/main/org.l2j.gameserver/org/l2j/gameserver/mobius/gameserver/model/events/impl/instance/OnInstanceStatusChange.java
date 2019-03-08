package org.l2j.gameserver.mobius.gameserver.model.events.impl.instance;

import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.mobius.gameserver.model.instancezone.Instance;

/**
 * @author malyelfik
 */
public final class OnInstanceStatusChange implements IBaseEvent
{
    private final Instance _world;
    private final int _status;

    public OnInstanceStatusChange(Instance world, int status)
    {
        _world = world;
        _status = status;
    }

    public Instance getWorld()
    {
        return _world;
    }

    public int getStatus()
    {
        return _status;
    }

    @Override
    public EventType getType()
    {
        return EventType.ON_INSTANCE_STATUS_CHANGE;
    }
}