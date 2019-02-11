package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.components.NpcString;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date 16:43/25.03.2011
 */
public abstract class NpcStringContainer extends L2GameServerPacket
{
	private final NpcString _npcString;
	private final String[] _parameters;

	protected NpcStringContainer(NpcString npcString, String... arg)
	{
		_npcString = npcString;
		_parameters = arg;
	}

	protected void writeElements(ByteBuffer buffer)
	{
		buffer.putInt(_npcString.getId());
		for(String st : _parameters)
			writeString(st, buffer);
	}
}
