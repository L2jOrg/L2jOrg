package l2s.gameserver.network.l2.s2c;

public class ExNotifyBirthDay extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExNotifyBirthDay();

	@Override
	protected void writeImpl()
	{
		writeD(0); // Actor OID 
	}
}