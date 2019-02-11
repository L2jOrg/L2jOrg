package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;
import org.l2j.gameserver.model.entity.events.objects.SiegeClanObject;
import org.l2j.gameserver.model.entity.residence.Residence;
import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.GameClient;

import static org.l2j.commons.util.Util.STRING_EMPTY;

/**
 * Populates the Siege Attacker List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xca<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = ca<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = registration valid (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Attackers Clans?<BR>
 * d = Number of Attackers Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @reworked VISTALL
 */
public class CastleSiegeAttackerListPacket extends L2GameServerPacket
{
	private int _id, _registrationValid;
	private List<SiegeClanObject> _clans = Collections.emptyList();

	public CastleSiegeAttackerListPacket(Residence residence)
	{
		_id = residence.getId();

		SiegeEvent<?,?> siegeEvent = residence.getSiegeEvent();
		if(siegeEvent != null)
		{
			_registrationValid = !siegeEvent.isRegistrationOver() ? 1 : 0;
			_clans = siegeEvent.getObjects(SiegeEvent.ATTACKERS);
		}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_id);

		buffer.putInt(0x00);
		buffer.putInt(_registrationValid);
		buffer.putInt(0x00);

		buffer.putInt(_clans.size());
		buffer.putInt(_clans.size());

		for(SiegeClanObject siegeClan : _clans)
		{
			Clan clan = siegeClan.getClan();

			buffer.putInt(clan.getClanId());
			writeString(clan.getName(), buffer);
			writeString(clan.getLeaderName(), buffer);
			buffer.putInt(clan.getCrestId());
			buffer.putInt((int) (siegeClan.getDate() / 1000L));

			Alliance alliance = clan.getAlliance();
			buffer.putInt(clan.getAllyId());
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
				buffer.putInt(0);
			}
		}
	}
}