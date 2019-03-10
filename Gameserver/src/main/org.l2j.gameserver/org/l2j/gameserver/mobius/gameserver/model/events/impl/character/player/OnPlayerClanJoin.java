package org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnPlayerClanJoin implements IBaseEvent {
    private final L2ClanMember _activeChar;
    private final L2Clan _clan;

    public OnPlayerClanJoin(L2ClanMember activeChar, L2Clan clan) {
        _activeChar = activeChar;
        _clan = clan;
    }

    public L2ClanMember getActiveChar() {
        return _activeChar;
    }

    public L2Clan getClan() {
        return _clan;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_CLAN_JOIN;
    }
}
