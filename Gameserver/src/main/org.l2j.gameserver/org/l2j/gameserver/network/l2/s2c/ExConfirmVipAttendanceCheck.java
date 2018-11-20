package org.l2j.gameserver.network.l2.s2c;

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
		writeByte(_success);
		writeByte(_receivedIndex);
		writeInt(0x00);
		writeInt(0x00);
	}
}