package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

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
		writeD(data.length);
		writeB(data);
	}
}
