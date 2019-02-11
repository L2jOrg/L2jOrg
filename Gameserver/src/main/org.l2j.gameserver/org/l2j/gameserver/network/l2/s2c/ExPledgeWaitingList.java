package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.l2j.gameserver.instancemanager.clansearch.ClanSearchManager;
import org.l2j.gameserver.model.clansearch.ClanSearchPlayer;
import org.l2j.gameserver.network.l2.GameClient;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ExPledgeWaitingList extends L2GameServerPacket
{
	private final Collection<ClanSearchPlayer> _applicants;

	public ExPledgeWaitingList(int clanId)
	{
		_applicants = ClanSearchManager.getInstance().applicantsCollection(clanId);
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_applicants.size());
		for(ClanSearchPlayer applicant : _applicants)
		{
			buffer.putInt(applicant.getCharId());
			writeString(applicant.getName(), buffer);
			buffer.putInt(applicant.getClassId());
			buffer.putInt(applicant.getLevel());
		}
	}
}