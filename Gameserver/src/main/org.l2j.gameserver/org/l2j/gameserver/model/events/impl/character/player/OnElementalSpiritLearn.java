package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnElementalSpiritLearn implements IBaseEvent {

    private final L2PcInstance player;

    public OnElementalSpiritLearn(L2PcInstance player) {
        this.player = player;
    }

    public L2PcInstance getPlayer() {
        return player;
    }

    @Override
    public EventType getType() {
        return EventType.ON_ELEMENTAL_SPIRIT_LEARN;
    }
}
