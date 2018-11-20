package org.l2j.gameserver.network.l2.s2c;

public class Ex2NDPasswordAckPacket extends L2GameServerPacket
{
	public static final int SUCCESS = 0x00;
	public static final int WRONG_PATTERN = 0x01;

	private int _response;

	public Ex2NDPasswordAckPacket(int response)
	{
		_response = response;
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0x00);
		writeInt(_response == WRONG_PATTERN ? 0x01 : 0x00);
		writeInt(0x00);
	}
}