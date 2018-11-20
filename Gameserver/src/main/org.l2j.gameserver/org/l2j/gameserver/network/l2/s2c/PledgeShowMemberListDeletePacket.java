package org.l2j.gameserver.network.l2.s2c;

public class PledgeShowMemberListDeletePacket extends L2GameServerPacket
{
	private String _player;

	public PledgeShowMemberListDeletePacket(String playerName)
	{
		_player = playerName;
	}

	@Override
	protected final void writeImpl()
	{
		writeString(_player);
	}
}