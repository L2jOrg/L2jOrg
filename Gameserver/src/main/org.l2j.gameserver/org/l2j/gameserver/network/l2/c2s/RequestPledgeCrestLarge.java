package org.l2j.gameserver.network.l2.c2s;

import java.util.Arrays;

import gnu.trove.map.TIntObjectMap;

import org.l2j.gameserver.cache.CrestCache;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExPledgeEmblem;

/**
 * @author Bonux
**/
public class RequestPledgeCrestLarge extends L2GameClientPacket
{
	// format: chdd
	private int _crestId;
	private int _pledgeId;

	@Override
	protected void readImpl()
	{
		_crestId = readInt();
		_pledgeId = readInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_crestId == 0)
			return;

		if(_pledgeId == 0)
			return;

		TIntObjectMap<byte[]> data = CrestCache.getInstance().getPledgeCrestLarge(_crestId);
		if(data != null)
		{
			int totalSize = CrestCache.getByteMapSize(data);
			int[] keys = data.keys();
			Arrays.sort(keys);
			for(int key : keys)
				sendPacket(new ExPledgeEmblem(_pledgeId, _crestId, key, totalSize, data.get(key)));
		}
	}
}