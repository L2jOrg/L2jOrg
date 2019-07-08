package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.data.elemental.ElementalSpirit;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnElementalSpiritUpgrade implements IBaseEvent {

    private final ElementalSpirit spirit;
    private final L2PcInstance player;

    public OnElementalSpiritUpgrade(L2PcInstance player, ElementalSpirit spirit) {
        this.player = player;
        this.spirit = spirit;
    }

    public ElementalSpirit getSpirit() {
        return spirit;
    }

    public L2PcInstance getPlayer() {
        return player;
    }


    @Override
    public EventType getType() {
        return EventType.ON_ELEMENTAL_SPIRIT_UPGRADE;
    }
}
