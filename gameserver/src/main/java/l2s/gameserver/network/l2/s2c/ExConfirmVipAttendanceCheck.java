package l2s.gameserver.network.l2.s2c;

public class ExConfirmVipAttendanceCheck extends L2GameServerPacket
{
	private final boolean _success;
	private final int _receivedIndex;

	public ExConfirmVipAttendanceCheck(boolean success, int receivedIndex)
	{
		_success = success;
		_receivedIndex = receivedIndex;
	}

	@Override
	protected void writeImpl()
	{
		writeC(_success);
		writeC(_receivedIndex);
		writeD(0x00);
		writeD(0x00);
	}
}