package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExVariationResult extends L2GameServerPacket
{
	private int _stat12;
	private int _stat34;
	private int _unk3;

	public ExVariationResult(int unk1, int unk2, int unk3)
	{
		_stat12 = unk1;
		_stat34 = unk2;
		_unk3 = unk3;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_stat12);
		buffer.putInt(_stat34);
		buffer.putInt(_unk3);
	}
}