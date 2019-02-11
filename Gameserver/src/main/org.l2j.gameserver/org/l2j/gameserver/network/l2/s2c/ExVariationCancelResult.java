package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExVariationCancelResult extends L2GameServerPacket
{
	private int _closeWindow;
	private int _unk1;

	public ExVariationCancelResult(int result)
	{
		_closeWindow = 1;
		_unk1 = result;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_unk1);
		buffer.putInt(_closeWindow);
	}
}