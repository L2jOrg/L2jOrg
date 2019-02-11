package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExCuriousHouseState extends L2GameServerPacket
{
	public static final L2GameServerPacket IDLE = new ExCuriousHouseState(0x00);
	public static final L2GameServerPacket INVITE = new ExCuriousHouseState(0x01);
	public static final L2GameServerPacket PREPARE = new ExCuriousHouseState(0x02);

	private int _state;

	public ExCuriousHouseState(int state)
	{
		_state = state;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_state);
	}
}
