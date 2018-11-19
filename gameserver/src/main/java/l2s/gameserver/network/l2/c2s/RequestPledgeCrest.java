package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.cache.CrestCache;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.PledgeCrestPacket;

public class RequestPledgeCrest extends L2GameClientPacket
{
	// format: cd

	private int _crestId;

	@Override
	protected void readImpl()
	{
		_crestId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
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