package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class ExTryMixCube extends L2GameServerPacket
{
	public static final L2GameServerPacket FAIL = new ExTryMixCube(6);

	private final int _result;
	private final int _itemId;
	private final long _itemCount;

	public ExTryMixCube(int result)
	{
		_result = result;
		_itemId = 0;
		_itemCount = 0;
	}

	public ExTryMixCube(int itemId, long itemCount)
	{
		_result = 0;
		_itemId = itemId;
		_itemCount = itemCount;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)_result);
		buffer.putInt(0x01); // UNK
		//for(int i = 0; i < count; i++)
		//{
			buffer.put((byte)0x00);
			buffer.putInt(_itemId);
			buffer.putLong(_itemCount);
		//}
	}
}