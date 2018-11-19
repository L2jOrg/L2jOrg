package l2s.gameserver.network.l2.s2c;

public class ExPartyMatchingRoomHistory extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeD(0);
	}
}