package org.l2j.gameserver.model.events.impl.sieges;

import org.l2j.gameserver.model.entity.FortSiege;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnFortSiegeFinish implements IBaseEvent {
    private final FortSiege _siege;

    public OnFortSiegeFinish(FortSiege siege) {
        _siege = siege;
    }

    public FortSiege getSiege() {
        return _siege;
    }

    @Override
    public EventType getType() {
        return EventType.ON_FORT_SIEGE_FINISH;
    }
}
