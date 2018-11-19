package l2s.gameserver.network.l2.s2c;

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
		writeC(0x00);
		writeD(_response == WRONG_PATTERN ? 0x01 : 0x00);
		writeD(0x00);
	}
}