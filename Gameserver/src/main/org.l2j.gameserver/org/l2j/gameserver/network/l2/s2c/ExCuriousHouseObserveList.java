package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

//открывается   окошко и написано ничья, кароче лисп победителя
public class ExCuriousHouseObserveList extends L2GameServerPacket
{
	public ExCuriousHouseObserveList(int currentId)
	{
		//
	}

	private static class ArenaInfo
	{
		public final int id;
		public final String unk;
		public final int status;
		public final int participants;

		public ArenaInfo(int id, String unk, int status, int participants)
		{
			this.id = id;
			this.unk = unk;
			this.status = status;
			this.participants = participants;
		}
	}

	private final List<ArenaInfo> _arenas = new ArrayList<ArenaInfo>();

	public ExCuriousHouseObserveList()
	{
		this(-1);
	}

	@Override
	protected void writeImpl()
	{
		writeD(_arenas.size());
		for (ArenaInfo arena : _arenas)
		{
			writeD(arena.id);
			writeS(arena.unk);
			writeH(arena.status);
			writeD(arena.participants);
		}
	}
}