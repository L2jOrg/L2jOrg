package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.instancemanager.clansearch.ClanSearchManager;
import l2s.gameserver.model.clansearch.ClanSearchPlayer;

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
		writeD(_applicants.size());
		for(ClanSearchPlayer applicant : _applicants)
		{
			writeD(applicant.getCharId());
			writeS(applicant.getName());
			writeD(applicant.getClassId());
			writeD(applicant.getLevel());
		}
	}
}