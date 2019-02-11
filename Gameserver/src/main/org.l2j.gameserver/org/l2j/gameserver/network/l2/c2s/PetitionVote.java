package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

/**
 * format: ddS
 */
public class PetitionVote extends L2GameClientPacket
{
	private int _type, _unk1;
	private String _petitionText;

	@Override
	protected void runImpl()
	{}

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_type = buffer.getInt();
		_unk1 = buffer.getInt(); // possible always zero
		_petitionText = readString(buffer, 4096);
		// not done
	}
}