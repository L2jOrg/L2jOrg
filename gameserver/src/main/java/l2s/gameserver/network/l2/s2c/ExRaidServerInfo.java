package l2s.gameserver.network.l2.s2c;

public class ExRaidServerInfo extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0x00);
		writeC(0x00);
	}
}
