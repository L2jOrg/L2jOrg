package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

public class ShowMinimapPacket extends L2GameServerPacket
{
	private int _mapId;

	public ShowMinimapPacket(Player player, int mapId)
	{
		_mapId = mapId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_mapId);
		writeC(0x00);
	}
}