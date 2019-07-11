package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnPlayerClanJoin implements IBaseEvent {
    private final ClanMember _activeChar;
    private final Clan _clan;

    public OnPlayerClanJoin(ClanMember activeChar, Clan clan) {
        _activeChar = activeChar;
        _clan = clan;
    }

    public ClanMember getActiveChar() {
        return _activeChar;
    }

    public Clan getClan() {
        return _clan;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_CLAN_JOIN;
    }
}
