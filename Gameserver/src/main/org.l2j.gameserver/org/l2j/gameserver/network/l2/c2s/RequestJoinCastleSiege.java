package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.dao.SiegeClanDAO;
import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.l2j.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;
import org.l2j.gameserver.model.entity.events.objects.SiegeClanObject;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.entity.residence.ClanHall;
import org.l2j.gameserver.model.entity.residence.Residence;
import org.l2j.gameserver.model.entity.residence.ResidenceType;
import org.l2j.gameserver.model.pledge.Alliance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.Privilege;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.CastleSiegeAttackerListPacket;
import org.l2j.gameserver.network.l2.s2c.CastleSiegeDefenderListPacket;
import org.l2j.gameserver.utils.SiegeUtils;

/**
 * @author VISTALL
 */
public class RequestJoinCastleSiege extends L2GameClientPacket
{
	private int _id;
	private boolean _isAttacker;
	private boolean _isJoining;

	@Override
	protected void readImpl()
	{
		_id = readInt();
		_isAttacker = readInt() == 1;
		_isJoining = readInt() == 1;
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR))
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}

		Residence residence = ResidenceHolder.getInstance().getResidence(_id);
		if(residence == null)
		{
			player.sendActionFailed();
			return;
		}

		if(residence.getType() == ResidenceType.CASTLE)
			registerAtCastle(player, (Castle) residence, _isAttacker, _isJoining);
		else if(residence.getType() == ResidenceType.CLANHALL && _isAttacker)
			registerAtClanHall(player, (ClanHall) residence, _isJoining);
	}

	private static void registerAtCastle(Player player, Castle castle, boolean attacker, boolean join)
	{
		CastleSiegeEvent siegeEvent = castle.getSiegeEvent();
		if(siegeEvent == null)
			return;

		Clan playerClan = player.getClan();

		if(playerClan.isPlacedForDisband())
		{
			player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN);
			return;
		}

		if(join)
		{
			Residence registeredCastle = null;
			for(Residence residence : ResidenceHolder.getInstance().getResidenceList(Castle.class))
			{
				CastleSiegeEvent residenceSiegeEvent = residence.getSiegeEvent();
				if(residenceSiegeEvent != null)
				{
					SiegeClanObject tempCastle = residenceSiegeEvent.getSiegeClan(CastleSiegeEvent.ATTACKERS, playerClan);

					if(tempCastle == null)
						tempCastle = residence.getSiegeEvent().getSiegeClan(CastleSiegeEvent.DEFENDERS, playerClan);

					if(tempCastle == null)
						tempCastle = residence.getSiegeEvent().getSiegeClan(CastleSiegeEvent.DEFENDERS_WAITING, playerClan);

					if(tempCastle != null)
						registeredCastle = residence;
				}
			}

			if(!siegeEvent.canRegisterOnSiege(player, playerClan, attacker))
				return;

			if(Config.ONLY_ONE_SIEGE_PER_CLAN)
			{
				if(registeredCastle != null)
				{
					player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
					return;
				}
			}

			if(castle.getSiegeDate().getTimeInMillis() == 0)
			{
				player.sendPacket(SystemMsg.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE);
				return;
			}

			if(siegeEvent.isRegistrationOver())
			{
				player.sendPacket(SystemMsg.YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER);
				return;
			}

			if(attacker)
			{
				int allSize = siegeEvent.getObjects(CastleSiegeEvent.ATTACKERS).size();
				if(allSize >= CastleSiegeEvent.MAX_SIEGE_CLANS)
				{
					player.sendPacket(SystemMsg.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE);
					return;
				}

				SiegeClanObject siegeClan = new SiegeClanObject(CastleSiegeEvent.ATTACKERS, playerClan, 0);
				siegeEvent.addObject(CastleSiegeEvent.ATTACKERS, siegeClan);

				SiegeClanDAO.getInstance().insert(castle, siegeClan);

				player.sendPacket(new CastleSiegeAttackerListPacket(castle));
			}
			else
			{
				SiegeClanObject siegeClan = new SiegeClanObject(CastleSiegeEvent.DEFENDERS_WAITING, playerClan, 0);
				siegeEvent.addObject(CastleSiegeEvent.DEFENDERS_WAITING, siegeClan);

				SiegeClanDAO.getInstance().insert(castle, siegeClan);

				player.sendPacket(new CastleSiegeDefenderListPacket(castle));
			}
		}
		else
		{
			SiegeClanObject siegeClan = null;
			if(attacker)
				siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.ATTACKERS, playerClan);
			else
			{
				siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.DEFENDERS, playerClan);
				if(siegeClan == null)
					siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.DEFENDERS_WAITING, playerClan);
				if(siegeClan == null)
					siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.DEFENDERS_REFUSED, playerClan);
			}

			if(siegeClan == null)
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_YET_REGISTERED_FOR_THE_CASTLE_SIEGE);
				return;
			}

			if(siegeEvent.isRegistrationOver())
			{
				player.sendPacket(SystemMsg.YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER);
				return;
			}

			siegeEvent.removeObject(siegeClan.getType(), siegeClan);

			SiegeClanDAO.getInstance().delete(castle, siegeClan);
			if(siegeClan.getType() == SiegeEvent.ATTACKERS)
				player.sendPacket(new CastleSiegeAttackerListPacket(castle));
			else
				player.sendPacket(new CastleSiegeDefenderListPacket(castle));
		}
	}

	private static void registerAtClanHall(Player player, ClanHall clanHall, boolean join)
	{
		ClanHallSiegeEvent siegeEvent = clanHall.getSiegeEvent();

		Clan playerClan = player.getClan();

		SiegeClanObject siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.ATTACKERS, playerClan);

		if(join)
		{
			if(playerClan.getHasHideout() != 0)
			{
				player.sendPacket(SystemMsg.A_CLAN_THAT_OWNS_A_CLAN_HALL_MAY_NOT_PARTICIPATE_IN_A_CLAN_HALL_SIEGE);
				return;
			}

			if(siegeClan != null)
			{
				player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
				return;
			}

			if(playerClan.getLevel() < 4)
			{
				player.sendPacket(SystemMsg.ONLY_CLANS_WHO_ARE_LEVEL_4_OR_ABOVE_CAN_REGISTER_FOR_BATTLE_AT_DEVASTATED_CASTLE_AND_FORTRESS_OF_THE_DEAD);
				return;
			}

			if(siegeEvent.isRegistrationOver())
			{
				player.sendPacket(SystemMsg.YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER);
				return;
			}

			int allSize = siegeEvent.getObjects(ClanHallSiegeEvent.ATTACKERS).size();
			if(allSize >= CastleSiegeEvent.MAX_SIEGE_CLANS)
			{
				player.sendPacket(SystemMsg.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE);
				return;
			}

			siegeClan = new SiegeClanObject(ClanHallSiegeEvent.ATTACKERS, playerClan, 0);
			siegeEvent.addObject(ClanHallSiegeEvent.ATTACKERS, siegeClan);

			SiegeClanDAO.getInstance().insert(clanHall, siegeClan);
		}
		else
		{
			if(siegeClan == null)
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_YET_REGISTERED_FOR_THE_CASTLE_SIEGE);
				return;
			}

			if(siegeEvent.isRegistrationOver())
			{
				player.sendPacket(SystemMsg.YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER);
				return;
			}

			siegeEvent.removeObject(siegeClan.getType(), siegeClan);

			SiegeClanDAO.getInstance().delete(clanHall, siegeClan);
		}

		player.sendPacket(new CastleSiegeAttackerListPacket(clanHall));
	}
}