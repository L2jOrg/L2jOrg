package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * ch Sddd
 */
public class ExMPCCPartyInfoUpdate extends L2GameServerPacket
{
	private Party _party;
	Player _leader;
	private int _mode, _count;

	/**
	 * @param party
	 * @param mode 0 = Remove, 1 = Add
	 */
	public ExMPCCPartyInfoUpdate(Party party, int mode)
	{
		_party = party;
		_mode = mode;
		_count = _party.getMemberCount();
		_leader = _party.getPartyLeader();
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_leader.getName(), buffer);
		buffer.putInt(_leader.getObjectId());
		buffer.putInt(_count);
		buffer.putInt(_mode); // mode 0 = Remove Party, 1 = AddParty, maybe more...
	}
}