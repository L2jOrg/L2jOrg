package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date 20:34/01.09.2011
 */
@StaticPacket
public class ExReplyHandOverPartyMaster extends L2GameServerPacket
{
	public static final L2GameServerPacket TRUE = new ExReplyHandOverPartyMaster(true);
	public static final L2GameServerPacket FALSE = new ExReplyHandOverPartyMaster(false);

	private boolean _isLeader;

	public ExReplyHandOverPartyMaster(boolean leader)
	{
		_isLeader = leader;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_isLeader ? 1 : 0);
	}
}
