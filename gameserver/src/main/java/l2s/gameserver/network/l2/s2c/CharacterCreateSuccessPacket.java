package l2s.gameserver.network.l2.s2c;

public class CharacterCreateSuccessPacket extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new CharacterCreateSuccessPacket();

	@Override
	protected final void writeImpl()
	{
		writeD(0x01);
	}
}