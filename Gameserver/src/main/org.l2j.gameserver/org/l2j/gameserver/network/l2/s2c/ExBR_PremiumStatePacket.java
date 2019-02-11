package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExBR_PremiumStatePacket extends L2GameServerPacket
{
	private int _objectId;
	private int _state;

	public ExBR_PremiumStatePacket(Player activeChar, boolean state)
	{
		_objectId = activeChar.getObjectId();
		_state = state ? 1 : 0;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
		buffer.put((byte)_state);
	}
}
