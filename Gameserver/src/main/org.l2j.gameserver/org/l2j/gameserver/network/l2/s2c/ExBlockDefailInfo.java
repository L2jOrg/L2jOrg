package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class ExBlockDefailInfo extends L2GameServerPacket
{
	private final String _blockName;
	private final String _blockMemo;

	public ExBlockDefailInfo(String name, String memo)
	{
		_blockName = name;
		_blockMemo = memo;

	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_blockName, buffer);
		writeString(_blockMemo, buffer);
	}
}
