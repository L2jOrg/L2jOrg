package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

public class ExUISettingPacket extends L2GameServerPacket
{
	private final byte data[];

	public ExUISettingPacket(Player player)
	{
		data = player.getKeyBindings();
	}

	@Override
	protected void writeImpl()
	{
		writeInt(data.length);
		writeB(data);
	}
}
