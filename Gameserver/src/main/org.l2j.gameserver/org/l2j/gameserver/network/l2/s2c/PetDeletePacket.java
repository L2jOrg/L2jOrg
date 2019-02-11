package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PetDeletePacket extends L2GameServerPacket
{
	private int _petId;
	private int _petnum;

	public PetDeletePacket(int petId, int petnum)
	{
		_petId = petId;
		_petnum = petnum;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_petnum);
		buffer.putInt(_petId);
	}
}