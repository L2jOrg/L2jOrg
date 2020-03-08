package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

import static org.l2j.gameserver.model.events.EventType.ON_PLAYER_CHARGE_SHOTS;

/**
 * @author JoeAlisson
 */
public class OnPlayerChargeShots implements IBaseEvent {

    private final Player player;
    private final ShotType shotType;

    public OnPlayerChargeShots(Player player, ShotType shotType) {
        this.player = player;
        this.shotType = shotType;
    }

    public Player getPlayer() {
        return player;
    }

    public ShotType getShotType() {
        return shotType;
    }

    @Override
    public EventType getType() {
        return ON_PLAYER_CHARGE_SHOTS;
    }
}
