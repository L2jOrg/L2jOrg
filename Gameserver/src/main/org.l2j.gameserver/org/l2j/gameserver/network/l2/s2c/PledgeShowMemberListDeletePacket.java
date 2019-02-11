package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PledgeShowMemberListDeletePacket extends L2GameServerPacket
{
	private String _player;

	public PledgeShowMemberListDeletePacket(String playerName)
	{
		_player = playerName;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_player, buffer);
	}
}