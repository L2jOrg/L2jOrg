package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;

public class ExPledgeEmblem extends L2GameServerPacket
{
	private int _clanId, _crestId, _crestPart, _totalSize;
	private byte[] _data;

	public ExPledgeEmblem(int clanId, int crestId, int crestPart, int totalSize, byte[] data)
	{
		_clanId = clanId;
		_crestId = crestId;
		_crestPart = crestPart;
		_totalSize = totalSize;
		_data = data;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(Config.REQUEST_ID);
		writeInt(_clanId);
		writeInt(_crestId);
		writeInt(_crestPart);
		writeInt(_totalSize);
		writeInt(_data.length);
		writeBytes(_data);
	}
}