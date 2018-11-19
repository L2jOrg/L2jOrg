package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.instancemanager.MatchingRoomManager;
import l2s.gameserver.model.Player;


public class ExListPartyMatchingWaitingRoom extends L2GameServerPacket
{
	private static final int ITEMS_PER_PAGE = 64;
	private final List<PartyMatchingWaitingInfo> _waitingList = new ArrayList<PartyMatchingWaitingInfo>(ITEMS_PER_PAGE);
	private final int _fullSize;

	public ExListPartyMatchingWaitingRoom(Player searcher, int minLevel, int maxLevel, int page, int[] classes)
	{
		final List<Player> temp = MatchingRoomManager.getInstance().getWaitingList(minLevel, maxLevel, classes);
		_fullSize = temp.size();

		final int first = Math.max((page - 1) * ITEMS_PER_PAGE, 0);
		final int firstNot = Math.min(page * ITEMS_PER_PAGE, _fullSize);
		for(int i = first; i < firstNot; i++)
			_waitingList.add(new PartyMatchingWaitingInfo(temp.get(i)));
	}

	@Override
	protected void writeImpl()
	{
		writeD(_fullSize);
		writeD(_waitingList.size());
		for(PartyMatchingWaitingInfo waitingInfo : _waitingList)
		{
			writeS(waitingInfo.name);
			writeD(waitingInfo.classId);
			writeD(waitingInfo.level);
			writeD(waitingInfo.locationId);
			writeD(waitingInfo.instanceReuses.size());
			for(int i : waitingInfo.instanceReuses)
				writeD(i);
		}
	}

	static class PartyMatchingWaitingInfo
	{
		public final int classId, level, locationId;
		public final String name;
		public final List<Integer> instanceReuses;

		public PartyMatchingWaitingInfo(Player member)
		{
			name = member.getName();
			classId = member.getClassId().getId();
			level = member.getLevel();
			locationId = MatchingRoomManager.getInstance().getLocation(member);
			instanceReuses = InstantZoneHolder.getInstance().getLockedInstancesList(member);
		}
	}
}