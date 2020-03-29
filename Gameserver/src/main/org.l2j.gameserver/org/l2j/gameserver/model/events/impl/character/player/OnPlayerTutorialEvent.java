package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author JoeAlisson
 */
public final class OnPlayerTutorialEvent implements IBaseEvent {
    private final Player player;
    private final int event;

    public OnPlayerTutorialEvent(Player activeChar, int event) {
        player = activeChar;
        this.event = event;
    }

    public Player getPlayer() {
        return player;
    }

    public int getEventId() {
        return event;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_TUTORIAL_EVENT;
    }
}
