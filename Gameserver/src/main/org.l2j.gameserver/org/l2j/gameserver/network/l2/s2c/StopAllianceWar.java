package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class StopAllianceWar extends L2GameServerPacket
{
	private String _allianceName;
	private String _char;

	public StopAllianceWar(String alliance, String charName)
	{
		_allianceName = alliance;
		_char = charName;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_allianceName, buffer);
		writeString(_char, buffer);
	}
}