package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExMultiSellResult extends L2GameServerPacket
{
	public static final L2GameServerPacket SUCCESS = new ExMultiSellResult();

	private final boolean _success;
	private final int _unk1;
	private final int _unk2;

	private ExMultiSellResult()
	{
		_success = true;
		_unk1 = 0;
		_unk2 = 0;
	}

	public ExMultiSellResult(int unk1, int unk2)
	{
		_success = false;
		_unk1 = unk1;
		_unk2 = unk2;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte) (_success ? 0x01 : 0x00));
		buffer.putInt(_unk1);
		buffer.putInt(_unk2);
	}
}