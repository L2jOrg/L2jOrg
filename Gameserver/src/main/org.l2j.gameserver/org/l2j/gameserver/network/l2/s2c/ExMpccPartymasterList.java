package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Set;

/**
 * @author VISTALL
 * @date 6:22/12.06.2011
 */
public class ExMpccPartymasterList extends L2GameServerPacket
{
	private Set<String> _members = Collections.emptySet();

	public ExMpccPartymasterList(Set<String> s)
	{
		_members = s;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_members.size());
		for(String t : _members)
			writeString(t, buffer);
	}
}
