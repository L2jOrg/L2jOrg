package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

/**
 * Format chS
 * c: (id) 0x39
 * h: (subid) 0x01
 * S: the summon name (or maybe cmd string ?)
 *
 */
class SuperCmdSummonCmd extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private String _summonName;

	/**
     * @param buf
     * @param client
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_summonName = readString(buffer);
	}

	@Override
	protected void runImpl()
	{}
}