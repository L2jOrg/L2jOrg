package l2s.gameserver.network.l2.s2c;

public class ExTodoListInzone extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		int instancesCount = 0;
		writeH(0);
		for(int i = 0; i < instancesCount; i++)
		{
			writeC(0x00);
			writeS("");
			writeS("");
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);
			writeC(0x00);
			writeC(0x00);
		}
	}
}