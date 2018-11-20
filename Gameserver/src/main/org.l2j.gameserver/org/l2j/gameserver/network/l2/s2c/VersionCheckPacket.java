package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;

public class VersionCheckPacket extends L2GameServerPacket
{
	private byte[] _key;

	public VersionCheckPacket(byte[] key)
	{
		_key = key;
	}

	@Override
	public void writeImpl()
	{
		if(_key == null || _key.length == 0)
		{
			writeByte(0x00);
			return;
		}
		writeByte(0x01);
		for(int i = 0; i < 8; i++)
			writeByte(_key[i]);
		writeInt(0x01);
		writeInt(Config.REQUEST_ID);	// Server ID
		writeByte(0x01);
		writeInt(0x00); // Seed (obfuscation key)
		writeByte(0x01);	// Classic?
		writeByte(0x00);	// Classic?
	}
}