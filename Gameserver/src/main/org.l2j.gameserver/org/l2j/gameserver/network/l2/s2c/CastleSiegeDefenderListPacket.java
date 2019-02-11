package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;
import org.l2j.gameserver.model.entity.events.objects.SiegeClanObject;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.GameClient;

import static org.l2j.commons.util.Util.STRING_EMPTY;

/**
 * Populates the Siege Defender List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xcb<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = 0xcb<BR>
 * d = unitId<BR>
 * d = unknow (0x00)<BR>
 * d = активация регистрации (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Defending Clans?<BR>
 * d = Number of Defending Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03 || Refuse = 0x04<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @reworked VISTALL
 */
public class CastleSiegeDefenderListPacket extends L2GameServerPacket
{
	public static int OWNER = 1;
	public static int WAITING = 2;
	public static int ACCEPTED = 3;
	public static int REFUSE = 4;

	private int _id, _registrationValid;
	private List<DefenderClan> _defenderClans = Collections.emptyList();

	public CastleSiegeDefenderListPacket(Castle castle)
	{
		_id = castle.getId();

		CastleSiegeEvent siegeEvent = castle.getSiegeEvent();
		if(siegeEvent != null)
		{
			_registrationValid = !siegeEvent.isRegistrationOver() && castle.getOwner() != null ? 1 : 0;

			List<SiegeClanObject> defenders = siegeEvent.getObjects(SiegeEvent.DEFENDERS);
			List<SiegeClanObject> defendersWaiting = siegeEvent.getObjects(CastleSiegeEvent.DEFENDERS_WAITING);
			List<SiegeClanObject> defendersRefused = siegeEvent.getObjects(CastleSiegeEvent.DEFENDERS_REFUSED);
			_defenderClans = new ArrayList<DefenderClan>(defenders.size() + defendersWaiting.size() + defendersRefused.size());
			if(castle.getOwner() != null)
				_defenderClans.add(new DefenderClan(castle.getOwner(), OWNER, 0));
			for(SiegeClanObject siegeClan : defenders)
				_defenderClans.add(new DefenderClan(siegeClan.getClan(), ACCEPTED, (int) (siegeClan.getDate() / 1000L)));
			for(SiegeClanObject siegeClan : defendersWaiting)
				_defenderClans.add(new DefenderClan(siegeClan.getClan(), WAITING, (int) (siegeClan.getDate() / 1000L)));
			for(SiegeClanObject siegeClan : defendersRefused)
				_defenderClans.add(new DefenderClan(siegeClan.getClan(), REFUSE, (int) (siegeClan.getDate() / 1000L)));
		}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_id);
		buffer.putInt(0x00);
		buffer.putInt(_registrationValid);
		buffer.putInt(0x00);

		buffer.putInt(_defenderClans.size());
		buffer.putInt(_defenderClans.size());
		for(DefenderClan defenderClan : _defenderClans)
		{
			Clan clan = defenderClan._clan;

			buffer.putInt(clan.getClanId());
			writeString(clan.getName(), buffer);
			writeString(clan.getLeaderName(), buffer);
			buffer.putInt(clan.getCrestId());
			buffer.putInt(defenderClan._time);
			buffer.putInt(defenderClan._type);
			buffer.putInt(clan.getAllyId());
			Alliance alliance = clan.getAlliance();
			if(alliance != null)
			{
				writeString(alliance.getAllyName(), buffer);
				writeString(alliance.getAllyLeaderName(), buffer);
				buffer.putInt(alliance.getAllyCrestId());
			}
			else
			{
				writeString(STRING_EMPTY, buffer);
				writeString(STRING_EMPTY, buffer);
				buffer.putInt(0x00);
			}
		}
	}

	private static class DefenderClan
	{
		private Clan _clan;
		private int _type;
		private int _time;

		public DefenderClan(Clan clan, int type, int time)
		{
			_clan = clan;
			_type = type;
			_time = time;
		}
	}
}