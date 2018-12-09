package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;

import static org.l2j.commons.util.Util.STRING_EMPTY;

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
		writeInt(_page);
		writeInt(_list.size());
		for(MatchingRoom room : _list)
		{
			writeInt(room.getId());
			Player leader = room.getLeader();
			writeString(leader == null ? STRING_EMPTY : leader.getName());
			writeInt(room.getPlayers().size());
			writeInt(room.getMinLevel());
			writeInt(room.getMaxLevel());
			writeInt(1);  //min group
			writeInt(room.getMaxMembersSize());   //max group
			writeString(room.getTopic());
		}

		writeInt(0);
		writeInt(0);
	}
}