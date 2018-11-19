package l2s.gameserver.network.l2.s2c;

/**
 * @author Erlandys
 */
public class ExDivideAdenaDone extends L2GameServerPacket
{
	private final int _friendsCount;
	private final long _count, _dividedCount;
	private final String _name;
	
	public ExDivideAdenaDone(int friendsCount, long count, long dividedCount, String name)
	{
		_friendsCount = friendsCount;
		_count = count;
		_dividedCount = dividedCount;
		_name = name;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x01); // Always 1
		writeC(0x00); // Always 0
		writeD(_friendsCount); // Friends count
		writeQ(_dividedCount); // Divided count
		writeQ(_count); // Whole count
		writeS(_name); // Giver name
	}
}