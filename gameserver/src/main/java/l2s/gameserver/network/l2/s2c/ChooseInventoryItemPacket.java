package l2s.gameserver.network.l2.s2c;

public class ChooseInventoryItemPacket extends L2GameServerPacket
{
	private int ItemID;

	public ChooseInventoryItemPacket(int id)
	{
		ItemID = id;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(ItemID);
	}
}