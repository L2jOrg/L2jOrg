package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

@SuppressWarnings("unused")
public class RequestSendMsnChatLog extends L2GameClientPacket
{
	private int unk3;
	private String unk, unk2;

	@Override
	protected void runImpl()
	{
		//logger.info.println(getType() + " :: " + unk + " :: " + unk2 + " :: " + unk3);
	}

	/**
	 * format: SSd
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		unk = readString(buffer);
		unk2 = readString(buffer);
		unk3 = buffer.getInt();
	}
}