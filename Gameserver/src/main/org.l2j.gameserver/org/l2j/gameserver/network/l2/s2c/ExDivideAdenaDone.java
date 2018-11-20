package org.l2j.gameserver.network.l2.s2c;

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
		writeByte(0x01); // Always 1
		writeByte(0x00); // Always 0
		writeInt(_friendsCount); // Friends count
		writeLong(_dividedCount); // Divided count
		writeLong(_count); // Whole count
		writeString(_name); // Giver name
	}
}