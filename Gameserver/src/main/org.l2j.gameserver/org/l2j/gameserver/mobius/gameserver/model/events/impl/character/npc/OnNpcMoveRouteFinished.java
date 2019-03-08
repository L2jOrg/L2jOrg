package org.l2j.gameserver.mobius.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnNpcMoveRouteFinished implements IBaseEvent
{
    private final L2Npc _npc;

    public OnNpcMoveRouteFinished(L2Npc npc)
    {
        _npc = npc;
    }

    public L2Npc getNpc()
    {
        return _npc;
    }

    @Override
    public EventType getType()
    {
        return EventType.ON_NPC_MOVE_ROUTE_FINISHED;
    }
}
