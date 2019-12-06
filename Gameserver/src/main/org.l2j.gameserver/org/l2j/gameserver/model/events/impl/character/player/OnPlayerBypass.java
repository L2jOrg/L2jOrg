package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnPlayerBypass implements IBaseEvent {
    private final Player player;
    private final String _command;

    public OnPlayerBypass(Player activeChar, String command) {
        player = activeChar;
        _command = command;
    }

    public Player getPlayer() {
        return player;
    }

    public String getCommand() {
        return _command;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_BYPASS;
    }
}
