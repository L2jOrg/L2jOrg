package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.ByteBuffer;

import static org.l2j.commons.configuration.Configurator.getSettings;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(getSettings(ServerSettings.class).serverId());
		buffer.putInt(_clanId);
		buffer.putInt(_crestId);
		buffer.putInt(_crestPart);
		buffer.putInt(_totalSize);
		buffer.putInt(_data.length);
		buffer.put(_data);
	}
}