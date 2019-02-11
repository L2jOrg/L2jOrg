package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author : Ragnarok
 * @date : 28.03.12  16:23
 */
public class ExCallToChangeClass extends L2GameServerPacket
{
	private int _classId;
	private boolean _showMsg;

	public ExCallToChangeClass(int classId, boolean showMsg)
	{
		_classId = classId;
		_showMsg = showMsg;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_classId); // New Class Id
		buffer.putInt(_showMsg ? 1 : 0); // Show Message
	}
}
