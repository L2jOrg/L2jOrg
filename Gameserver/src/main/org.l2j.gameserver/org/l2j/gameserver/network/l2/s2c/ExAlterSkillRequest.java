package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @autor Monithly
 */
public class ExAlterSkillRequest extends L2GameServerPacket
{
	private final int _activeId, _requestId, _duration;

	public ExAlterSkillRequest(int requestId, int activeId, int duration)
	{
		_requestId = requestId;
		_activeId = activeId;
		_duration = duration;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_requestId);
		buffer.putInt(_activeId);
		buffer.putInt(_duration);
	}
}
