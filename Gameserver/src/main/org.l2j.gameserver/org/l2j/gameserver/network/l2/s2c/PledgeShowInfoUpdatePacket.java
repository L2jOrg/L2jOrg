package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.model.entity.residence.ClanHall;

import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.ByteBuffer;

import static org.l2j.commons.configuration.Configurator.getSettings;
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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		//sending empty data so client will ask all the info in response ;)
		buffer.putInt(clan_id);
		buffer.putInt(getSettings(ServerSettings.class).serverId());
		buffer.putInt(crest_id);
		buffer.putInt(clan_level);
		buffer.putInt(_hasCastle);
		if(_hasInstantClanHall > 0)
		{
			buffer.putInt(0x01);
			buffer.putInt(_hasInstantClanHall);
		}
		else if(_hasClanHall != 0)
		{
			buffer.putInt(0x00);
			buffer.putInt(_hasClanHall);
		}
		else
		{
			buffer.putInt(0x00);
			buffer.putInt(0x00);
		}
		buffer.putInt(0x00);
		buffer.putInt(clan_rank);// displayed in the "tree" view (with the clan skills)
		buffer.putInt(clan_rep);
		buffer.putInt(_isDisbanded ? 3 : 0);
		buffer.putInt(0);
		buffer.putInt(ally_id); //c5
		writeString(ally_name, buffer); //c5
		buffer.putInt(ally_crest); //c5
		buffer.putInt(atwar ? 0x01 : 0x00); //c5

		buffer.putInt(0x00);
		buffer.putInt(0x00);
	}
}