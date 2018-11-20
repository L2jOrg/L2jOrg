package org.l2j.gameserver.network.l2.s2c;

import java.util.Collection;

import org.l2j.gameserver.instancemanager.clansearch.ClanSearchManager;
import org.l2j.gameserver.model.clansearch.ClanSearchPlayer;

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
	protected void writeImpl()
	{
		writeInt(_applicants.size());
		for(ClanSearchPlayer applicant : _applicants)
		{
			writeInt(applicant.getCharId());
			writeString(applicant.getName());
			writeInt(applicant.getClassId());
			writeInt(applicant.getLevel());
		}
	}
}