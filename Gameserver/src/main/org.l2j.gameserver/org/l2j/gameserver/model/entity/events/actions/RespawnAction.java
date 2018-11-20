package org.l2j.gameserver.model.entity.events.actions;

import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.model.entity.events.EventAction;

public class RespawnAction implements EventAction
{
    private final String _name;

    public RespawnAction(String name)
    {
        _name = name;
    }

    @Override
    public void call(Event event)
    {
        event.respawnAction(_name);
    }
}