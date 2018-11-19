package l2s.gameserver.network.l2.s2c;

/**
 * Format: ch
 * Протокол 828: при отправке пакета клиенту ничего не происходит.
 */
public class ExPlayScene extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeD(0x00); //Kamael
	}
}