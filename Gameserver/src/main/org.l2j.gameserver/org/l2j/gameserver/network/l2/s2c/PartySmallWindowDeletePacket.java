package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PartySmallWindowDeletePacket extends L2GameServerPacket
{
	private final int _objId;
	private final String _name;

	public PartySmallWindowDeletePacket(Player member)
	{
		_objId = member.getObjectId();
		_name = member.getName();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objId);
		writeString(_name, buffer);
	}
}