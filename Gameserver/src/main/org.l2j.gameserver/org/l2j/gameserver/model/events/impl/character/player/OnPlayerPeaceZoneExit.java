package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

import static org.l2j.gameserver.model.events.EventType.ON_PLAYER_PEACE_ZONE_EXIT;

public class OnPlayerPeaceZoneExit implements IBaseEvent {

    private final Player player;

    public OnPlayerPeaceZoneExit(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public EventType getType() {
        return ON_PLAYER_PEACE_ZONE_EXIT;
    }
}
