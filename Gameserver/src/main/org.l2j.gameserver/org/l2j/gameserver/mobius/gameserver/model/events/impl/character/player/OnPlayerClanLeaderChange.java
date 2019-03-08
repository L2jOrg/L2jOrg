package org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player;


import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnPlayerClanLeaderChange implements IBaseEvent
{
    private final L2ClanMember _oldLeader;
    private final L2ClanMember _newLeader;
    private final L2Clan _clan;

    public OnPlayerClanLeaderChange(L2ClanMember oldLeader, L2ClanMember newLeader, L2Clan clan)
    {
        _oldLeader = oldLeader;
        _newLeader = newLeader;
        _clan = clan;
    }

    public L2ClanMember getOldLeader()
    {
        return _oldLeader;
    }

    public L2ClanMember getNewLeader()
    {
        return _newLeader;
    }

    public L2Clan getClan()
    {
        return _clan;
    }

    @Override
    public EventType getType()
    {
        return EventType.ON_PLAYER_CLAN_LEADER_CHANGE;
    }
}
