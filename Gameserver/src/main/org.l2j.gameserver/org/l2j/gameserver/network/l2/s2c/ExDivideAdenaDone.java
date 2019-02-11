package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)0x01); // Always 1
		buffer.put((byte)0x00); // Always 0
		buffer.putInt(_friendsCount); // Friends count
		buffer.putLong(_dividedCount); // Divided count
		buffer.putLong(_count); // Whole count
		writeString(_name, buffer); // Giver name
	}
}