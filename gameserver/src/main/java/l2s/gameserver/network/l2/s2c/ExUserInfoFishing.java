package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

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
		writeD(_activeChar.getObjectId());

		if(_activeChar.getFishing().isInProcess())
		{
			writeC(0x01);
			writeD(_activeChar.getFishing().getHookLocation().getX());
			writeD(_activeChar.getFishing().getHookLocation().getY());
			writeD(_activeChar.getFishing().getHookLocation().getZ());
		}
		else
			writeC(0x00);
	}
}
