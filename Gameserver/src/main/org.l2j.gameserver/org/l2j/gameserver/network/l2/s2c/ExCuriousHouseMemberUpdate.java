package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

public class ExCuriousHouseMemberUpdate extends L2GameServerPacket
{
	private Player _player;

	public ExCuriousHouseMemberUpdate(Player player)
	{
		_player = player;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_player.getObjectId());
		writeD(_player.getMaxHp());
		writeD(_player.getMaxCp());
		writeD((int)_player.getCurrentHp());
		writeD((int)_player.getCurrentCp());
	}
}

