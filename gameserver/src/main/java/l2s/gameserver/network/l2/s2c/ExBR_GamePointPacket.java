package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

public class ExBR_GamePointPacket extends L2GameServerPacket
{
	private int _objectId;
	private long _points;

	public ExBR_GamePointPacket(Player player)
	{
		_objectId = player.getObjectId();
		_points = player.getPremiumPoints();
	}

	@Override
	protected void writeImpl()
	{
		writeD(_objectId);
		writeQ(_points);
		writeD(0x00); //??
	}
}