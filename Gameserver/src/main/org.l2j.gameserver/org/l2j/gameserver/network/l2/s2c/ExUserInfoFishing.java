package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExUserInfoFishing extends L2GameServerPacket
{
	private Player _activeChar;

	public ExUserInfoFishing(Player character)
	{
		_activeChar = character;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_activeChar.getObjectId());

		if(_activeChar.getFishing().isInProcess())
		{
			buffer.put((byte)0x01);
			buffer.putInt(_activeChar.getFishing().getHookLocation().getX());
			buffer.putInt(_activeChar.getFishing().getHookLocation().getY());
			buffer.putInt(_activeChar.getFishing().getHookLocation().getZ());
		}
		else
			buffer.put((byte)0x00);
	}
}
