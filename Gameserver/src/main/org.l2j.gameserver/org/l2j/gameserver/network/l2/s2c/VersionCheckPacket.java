package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.ByteBuffer;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class VersionCheckPacket extends L2GameServerPacket
{
	private byte[] _key;

	public VersionCheckPacket(byte[] key)
	{
		_key = key;
	}

	@Override
	public void writeImpl(GameClient client, ByteBuffer buffer)
	{
		if(_key == null || _key.length == 0)
		{
			buffer.put((byte)0x00);
			return;
		}
		buffer.put((byte)0x01);
		for(int i = 0; i < 8; i++)
			buffer.put((byte)_key[i]);
		buffer.putInt(0x01);
		buffer.putInt(getSettings(ServerSettings.class).serverId());	// Server ID
		buffer.put((byte)0x01);
		buffer.putInt(0x00); // Seed (obfuscation key)
		buffer.put((byte)0x01);	// Classic?
		buffer.put((byte)0x00);	// Classic?
	}
}