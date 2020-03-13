package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnPlayerClanLvlUp implements IBaseEvent {
    private final Clan _clan;

    public OnPlayerClanLvlUp(Player activeChar, Clan clan) {
        _clan = clan;
    }

    public Clan getClan() {
        return _clan;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_CLAN_LVLUP;
    }
}
