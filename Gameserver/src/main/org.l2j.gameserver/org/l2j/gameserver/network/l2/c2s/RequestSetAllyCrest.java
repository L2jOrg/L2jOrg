package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.cache.CrestCache;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.pledge.Alliance;

import java.nio.ByteBuffer;

public class RequestSetAllyCrest extends L2GameClientPacket
{
	private int _length;
	private byte[] _data;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_length = buffer.getInt();
		if(_length == CrestCache.ALLY_CREST_SIZE && _length == buffer.remaining())
		{
			_data = new byte[_length];
			buffer.get(_data);
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		Alliance ally = activeChar.getAlliance();
		if(ally != null && activeChar.isAllyLeader())
		{
			int crestId = 0;

			if(_data != null)
				crestId = CrestCache.getInstance().saveAllyCrest(ally.getAllyId(), _data);
			else if(ally.hasAllyCrest())
				CrestCache.getInstance().removeAllyCrest(ally.getAllyId());

			ally.setAllyCrestId(crestId);
			ally.broadcastAllyStatus();
		}
	}
}