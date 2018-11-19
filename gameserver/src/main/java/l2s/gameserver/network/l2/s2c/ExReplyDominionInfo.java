package l2s.gameserver.network.l2.s2c;

public class ExReplyDominionInfo extends L2GameServerPacket
{
	public ExReplyDominionInfo()
	{
		//
	}

	@Override
	protected void writeImpl()
	{
		writeD(0x00);
	}
}