package l2s.gameserver.network.l2.s2c;

public class ServerCloseSocketPacket extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ServerCloseSocketPacket();

	@Override
	protected void writeImpl()
	{
	}
}