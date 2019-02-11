package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExCuriousHouseMemberUpdate extends L2GameServerPacket
{
	private Player _player;

	public ExCuriousHouseMemberUpdate(Player player)
	{
		_player = player;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_player.getObjectId());
		buffer.putInt(_player.getMaxHp());
		buffer.putInt(_player.getMaxCp());
		buffer.putInt((int)_player.getCurrentHp());
		buffer.putInt((int)_player.getCurrentCp());
	}
}

