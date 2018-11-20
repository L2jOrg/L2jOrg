package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

public class ExUserInfoFishing extends L2GameServerPacket
{
	private Player _activeChar;

	public ExUserInfoFishing(Player character)
	{
		_activeChar = character;
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_activeChar.getObjectId());

		if(_activeChar.getFishing().isInProcess())
		{
			writeByte(0x01);
			writeInt(_activeChar.getFishing().getHookLocation().getX());
			writeInt(_activeChar.getFishing().getHookLocation().getY());
			writeInt(_activeChar.getFishing().getHookLocation().getZ());
		}
		else
			writeByte(0x00);
	}
}
