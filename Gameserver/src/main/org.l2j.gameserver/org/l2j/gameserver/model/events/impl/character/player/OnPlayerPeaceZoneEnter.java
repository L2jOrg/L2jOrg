package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.world.zone.type.PeaceZone;

import static org.l2j.gameserver.model.events.EventType.ON_PLAYER_PEACE_ZONE_ENTER;

/**
 * @author JoeAlisson
 */
public class OnPlayerPeaceZoneEnter implements IBaseEvent {

    private final Player player;
    private final PeaceZone zone;

    public OnPlayerPeaceZoneEnter(Player player, PeaceZone peaceZone) {
        this.player = player;
        this.zone = peaceZone;
    }

    public Player getPlayer() {
        return player;
    }

    public PeaceZone getZone() {
        return zone;
    }

    @Override
    public EventType getType() {
        return ON_PLAYER_PEACE_ZONE_ENTER;
    }
}
