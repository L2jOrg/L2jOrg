package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.SubUnit;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ExPledgeRecruitInfo extends L2GameServerPacket
{
	private final String _clanName;
	private final String _leaderName;
	private final int _clanLevel;
	private final int _clanMemberCount;
	private final List<SubUnit> _subUnits = new ArrayList<SubUnit>();

	public ExPledgeRecruitInfo(Clan clan)
	{
		_clanName = clan.getName();
		_leaderName = clan.getLeader().getName();
		_clanLevel = clan.getLevel();
		_clanMemberCount = clan.getAllSize();

		for(SubUnit su : clan.getAllSubUnits())
		{
			if(su.getType() == Clan.SUBUNIT_MAIN_CLAN)
				continue;

			_subUnits.add(su);
		}
	}

	protected void writeImpl()
	{
		writeS(_clanName);
		writeS(_leaderName);
		writeD(_clanLevel);
		writeD(_clanMemberCount);
		writeD(_subUnits.size());
		for(SubUnit su : _subUnits)
		{
			writeD(su.getType());
			writeS(su.getName());
		}
	}
}