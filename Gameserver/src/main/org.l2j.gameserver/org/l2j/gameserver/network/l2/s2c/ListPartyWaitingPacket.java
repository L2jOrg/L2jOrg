package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.l2.GameClient;

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
    protected final void writeImpl(GameClient client, ByteBuffer buffer)
    {
        buffer.putInt(_page);
        buffer.putInt(_rooms.size());

        for(MatchingRoom room : _rooms)
        {
            buffer.putInt(room.getId()); //room id
            writeString(room.getTopic(), buffer); // room name
            buffer.putInt(room.getLocationId());
            buffer.putInt(room.getMinLevel()); //min level
            buffer.putInt(room.getMaxLevel()); //max level
            buffer.putInt(room.getMaxMembersSize()); //max members coun
            writeString(room.getLeader() == null ? "None" : room.getLeader().getName(), buffer);

            Collection<Player> players = room.getPlayers();
            buffer.putInt(players.size()); //members count
            for(Player player : players)
            {
                buffer.putInt(player.getClassId().getId());
                writeString(player.getName(), buffer);
            }
        }
        buffer.putInt(0x00);
        buffer.putInt(0x00);
    }
}