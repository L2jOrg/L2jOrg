package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.cache.CrestCache;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.PledgeCrestPacket;

import java.nio.ByteBuffer;

public class RequestPledgeCrest extends L2GameClientPacket
{
	// format: cd

	private int _crestId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_crestId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;
		if(_crestId == 0)
			return;
		byte[] data = CrestCache.getInstance().getPledgeCrest(_crestId);
		if(data != null)
		{
			PledgeCrestPacket pc = new PledgeCrestPacket(_crestId, data);
			sendPacket(pc);
		}
	}
}