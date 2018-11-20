package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

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
	protected void writeImpl()
	{
		writeD(_objectId);
		writeC(_state);
	}
}
