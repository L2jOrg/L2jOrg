package org.l2j.gameserver.network.l2.s2c;

public class ExShowUsmPacket extends L2GameServerPacket
{
	private int _usmVideoId;

	public ExShowUsmPacket(int usmVideoId)
	{
		_usmVideoId = usmVideoId;
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_usmVideoId);
	}
}