package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.pledge.SubUnit;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PledgeReceiveSubPledgeCreated extends L2GameServerPacket
{
	private int type;
	private String _name, leader_name;

	public PledgeReceiveSubPledgeCreated(SubUnit subPledge)
	{
		type = subPledge.getType();
		_name = subPledge.getName();
		leader_name = subPledge.getLeaderName();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x01);
		buffer.putInt(type);
		writeString(_name, buffer);
		writeString(leader_name, buffer);
	}
}