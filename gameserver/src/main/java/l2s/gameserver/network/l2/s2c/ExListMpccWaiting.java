package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import l2s.gameserver.instancemanager.MatchingRoomManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.matching.MatchingRoom;

/**
 * @author VISTALL
 */
public class ExListMpccWaiting extends L2GameServerPacket
{
	private static final int ITEMS_PER_PAGE = 10;
	private int _page;
	private List<MatchingRoom> _list;

	public ExListMpccWaiting(Player player, int page, int location, boolean allLevels)
	{
		int first = (page - 1) * ITEMS_PER_PAGE;
		int firstNot = page * ITEMS_PER_PAGE;

		List<MatchingRoom> temp = MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.CC_MATCHING, location, allLevels, player);
		_page = page;
		_list = new ArrayList<MatchingRoom>(ITEMS_PER_PAGE);

		for(int i = 0; i < temp.size(); i++)
		{
			if (i < first || i >= firstNot)
				continue;

			_list.add(temp.get(i));
		}
	}

	@Override
	public void writeImpl()
	{
		writeD(_page);
		writeD(_list.size());
		for(MatchingRoom room : _list)
		{
			writeD(room.getId());
			Player leader = room.getLeader();
			writeS(leader == null ? StringUtils.EMPTY : leader.getName());
			writeD(room.getPlayers().size());
			writeD(room.getMinLevel());
			writeD(room.getMaxLevel());
			writeD(1);  //min group
			writeD(room.getMaxMembersSize());   //max group
			writeS(room.getTopic());
		}

		writeD(0);
		writeD(0);
	}
}