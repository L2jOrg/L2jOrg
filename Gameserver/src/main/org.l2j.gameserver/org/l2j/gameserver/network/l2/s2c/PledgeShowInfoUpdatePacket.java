package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.model.entity.residence.ClanHall;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;

import static org.l2j.commons.util.Util.STRING_EMPTY;

public class PledgeShowInfoUpdatePacket extends L2GameServerPacket
{
	private int clan_id, clan_level, clan_rank, clan_rep, crest_id, ally_id, ally_crest;
	private final boolean atwar;
	private String ally_name = STRING_EMPTY;
	private int _hasCastle, _hasClanHall, _hasInstantClanHall;
	private boolean _isDisbanded;

	public PledgeShowInfoUpdatePacket(final Clan clan)
	{
		clan_id = clan.getClanId();
		clan_level = clan.getLevel();

		_hasCastle = clan.getCastle();

		ClanHall clanHall = ResidenceHolder.getInstance().getResidence(ClanHall.class, clan.getHasHideout());
		if(clanHall != null)
		{
			_hasClanHall = clanHall.getId();
			_hasInstantClanHall = clanHall.getInstantZoneId();
		}
		else
		{
			_hasClanHall = 0;
			_hasInstantClanHall = 0;
		}

		clan_rank = clan.getRank();
		clan_rep = clan.getReputationScore();
		crest_id = clan.getCrestId();
		ally_id = clan.getAllyId();
		atwar = clan.isAtWar();
		_isDisbanded = clan.isPlacedForDisband();
		Alliance ally = clan.getAlliance();
		if(ally != null)
		{
			ally_name = ally.getAllyName();
			ally_crest = ally.getAllyCrestId();
		}
	}

	@Override
	protected final void writeImpl()
	{
		//sending empty data so client will ask all the info in response ;)
		writeInt(clan_id);
		writeInt(Config.REQUEST_ID);
		writeInt(crest_id);
		writeInt(clan_level);
		writeInt(_hasCastle);
		if(_hasInstantClanHall > 0)
		{
			writeInt(0x01);
			writeInt(_hasInstantClanHall);
		}
		else if(_hasClanHall != 0)
		{
			writeInt(0x00);
			writeInt(_hasClanHall);
		}
		else
		{
			writeInt(0x00);
			writeInt(0x00);
		}
		writeInt(0x00);
		writeInt(clan_rank);// displayed in the "tree" view (with the clan skills)
		writeInt(clan_rep);
		writeInt(_isDisbanded ? 3 : 0);
		writeInt(0);
		writeInt(ally_id); //c5
		writeString(ally_name); //c5
		writeInt(ally_crest); //c5
		writeInt(atwar); //c5

		writeInt(0x00);
		writeInt(0x00);
	}
}