package l2s.gameserver.network.l2.c2s;

public class RequestChangeBookMarkSlot extends L2GameClientPacket
{
	private int slot_old, slot_new;

	@Override
	protected void readImpl()
	{
		slot_old = readD();
		slot_new = readD();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}