package org.l2j.gameserver.network.l2.s2c;

import java.util.List;

import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.ClanWar;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class PledgeReceiveWarList extends L2GameServerPacket
{
	private Clan _clan;
	private int _state;
	private int _page;

	public PledgeReceiveWarList(Clan clan, int state, int page)
	{
		_clan = clan;
		_page = page;
		_state = state;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_page);

		List<ClanWar> wars = _clan.getClanWars();

		writeInt(wars.size());
		for(ClanWar war : wars)
		{
			Clan opposingClan = war.getAttackerClan();
			if(opposingClan == _clan)
				opposingClan = war.getOpposingClan();

			if(opposingClan == null)
				continue;

			int pointDiff = war.getPointDiff(_clan);
			int duration = (int) (war.getPeriodDuration() / 1000L);
			if(war.getClanWarState(_clan).ordinal() >= 3)
				duration += 172800;
			else if(war.getClanWarState(_clan).ordinal() <= 1)
				duration += 345600;

			writeString(opposingClan.getName());
			writeInt(war.getClanWarState(_clan).ordinal());
			writeInt(duration);

			writeInt(pointDiff);
			writeInt(war.calculateWarProgress(pointDiff).ordinal());
			writeInt(opposingClan.getAllSize());
		}
	}
}