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
		writeInt(_player.getObjectId());
		writeInt(_player.getMaxHp());
		writeInt(_player.getMaxCp());
		writeInt((int)_player.getCurrentHp());
		writeInt((int)_player.getCurrentCp());
	}
}

