package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA. User: Cain Date: 23.05.12 Time: 23:09 ответ от чара
 * выбранного на замену
 */
public class RequestAcceptWaitingSubstitute extends L2GameClientPacket
{
	private int _flag;
	private int _unk1;
	private int _unk2;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_flag = buffer.getInt();
		_unk1 = buffer.getInt();
		_unk2 = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		//
	}
}
