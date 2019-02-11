package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;
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
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_arenas.size());
		for (ArenaInfo arena : _arenas)
		{
			buffer.putInt(arena.id);
			writeString(arena.unk, buffer);
			buffer.putShort((short) arena.status);
			buffer.putInt(arena.participants);
		}
	}
}