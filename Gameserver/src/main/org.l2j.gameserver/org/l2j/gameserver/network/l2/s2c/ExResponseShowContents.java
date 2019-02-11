package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class ExResponseShowContents extends L2GameServerPacket
{
	private final String _contents;

	public ExResponseShowContents(String contents)
	{
		_contents = contents;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_contents, buffer);
	}
}