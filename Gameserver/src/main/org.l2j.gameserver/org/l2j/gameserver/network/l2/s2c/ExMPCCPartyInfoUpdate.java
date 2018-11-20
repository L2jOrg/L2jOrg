package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;

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
	protected void writeImpl()
	{
		writeString(_leader.getName());
		writeInt(_leader.getObjectId());
		writeInt(_count);
		writeInt(_mode); // mode 0 = Remove Party, 1 = AddParty, maybe more...
	}
}