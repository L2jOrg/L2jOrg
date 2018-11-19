package l2s.gameserver.network.l2.s2c;

/**
 * @author VISTALL
 */
public class ExShowOwnthingPos extends L2GameServerPacket
{
	public ExShowOwnthingPos()
	{
		//
	}

	@Override
	protected void writeImpl()
	{
		writeD(0x00);
	}
}