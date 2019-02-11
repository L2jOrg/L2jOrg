package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public final class ExEnchantSucess extends L2GameServerPacket
{
	private final int _itemId;

	public ExEnchantSucess(int itemId)
	{
		_itemId = itemId;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_itemId);
	}
}