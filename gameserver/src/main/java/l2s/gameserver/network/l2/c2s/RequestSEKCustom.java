package l2s.gameserver.network.l2.c2s;

public class RequestSEKCustom extends L2GameClientPacket
{
	private int SlotNum, Direction;

	/**
	 * format: dd
	 */
	@Override
	protected void readImpl()
	{
		SlotNum = readD();
		Direction = readD();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}