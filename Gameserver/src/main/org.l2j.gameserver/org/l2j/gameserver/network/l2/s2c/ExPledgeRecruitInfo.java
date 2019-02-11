package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.SubUnit;
import org.l2j.gameserver.network.l2.GameClient;

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

	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_clanName, buffer);
		writeString(_leaderName, buffer);
		buffer.putInt(_clanLevel);
		buffer.putInt(_clanMemberCount);
		buffer.putInt(_subUnits.size());
		for(SubUnit su : _subUnits)
		{
			buffer.putInt(su.getType());
			writeString(su.getName(), buffer);
		}
	}
}