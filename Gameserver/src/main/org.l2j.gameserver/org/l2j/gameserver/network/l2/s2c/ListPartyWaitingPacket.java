package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;

public class ListPartyWaitingPacket extends L2GameServerPacket
{
    private static final int ITEMS_PER_PAGE = 16;
    private final Collection<MatchingRoom> _rooms = new ArrayList<MatchingRoom>(ITEMS_PER_PAGE);
    private final int _page;

    public ListPartyWaitingPacket(int region, boolean allLevels, int page, Player activeChar)
    {
        _page = page;
        final List<MatchingRoom> temp = MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.PARTY_MATCHING, region, allLevels, activeChar);

        final int first = Math.max((page - 1) * ITEMS_PER_PAGE, 0);
        final int firstNot = Math.min(page * ITEMS_PER_PAGE, temp.size());
        for(int i = first; i < firstNot; i++)
            _rooms.add(temp.get(i));
    }

    @Override
    protected final void writeImpl()
    {
        writeInt(_page);
        writeInt(_rooms.size());

        for(MatchingRoom room : _rooms)
        {
            writeInt(room.getId()); //room id
            writeString(room.getTopic()); // room name
            writeInt(room.getLocationId());
            writeInt(room.getMinLevel()); //min level
            writeInt(room.getMaxLevel()); //max level
            writeInt(room.getMaxMembersSize()); //max members coun
            writeString(room.getLeader() == null ? "None" : room.getLeader().getName());

            Collection<Player> players = room.getPlayers();
            writeInt(players.size()); //members count
            for(Player player : players)
            {
                writeInt(player.getClassId().getId());
                writeString(player.getName());
            }
        }
        writeInt(0x00);
        writeInt(0x00);
    }
}