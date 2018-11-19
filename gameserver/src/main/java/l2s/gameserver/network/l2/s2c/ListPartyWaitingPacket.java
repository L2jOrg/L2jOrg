package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2s.gameserver.instancemanager.MatchingRoomManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.matching.MatchingRoom;

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
        writeD(_page);
        writeD(_rooms.size());

        for(MatchingRoom room : _rooms)
        {
            writeD(room.getId()); //room id
            writeS(room.getTopic()); // room name
            writeD(room.getLocationId());
            writeD(room.getMinLevel()); //min level
            writeD(room.getMaxLevel()); //max level
            writeD(room.getMaxMembersSize()); //max members coun
            writeS(room.getLeader() == null ? "None" : room.getLeader().getName());

            Collection<Player> players = room.getPlayers();
            writeD(players.size()); //members count
            for(Player player : players)
            {
                writeD(player.getClassId().getId());
                writeS(player.getName());
            }
        }
        writeD(0x00);
        writeD(0x00);
    }
}