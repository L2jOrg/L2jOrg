package org.l2j.gameserver.handler.admincommands.impl;

import java.util.StringTokenizer;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.admincommands.IAdminCommandHandler;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.VillageMasterInstance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.SubUnit;
import org.l2j.gameserver.model.pledge.UnitMember;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.PledgeShowInfoUpdatePacket;
import org.l2j.gameserver.tables.ClanTable;
import org.l2j.gameserver.utils.Util;

/**
 * Pledge Manipulation //pledge <create|setlevel|resetcreate|resetwait|addrep|setleader>
 */
public class AdminPledge implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_pledge
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		@SuppressWarnings("unused")
		Commands command = (Commands) comm;

		if(activeChar.getPlayerAccess() == null || !activeChar.getPlayerAccess().CanEditPledge || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			return false;

		Player target = (Player) activeChar.getTarget();

		if(fullString.startsWith("admin_pledge"))
		{
			StringTokenizer st = new StringTokenizer(fullString);
			st.nextToken();

			String action = st.nextToken(); // setlevel|resetcreate|resetwait|addrep

			if(action.equals("create"))
				try
				{
					if(target == null)
					{
						activeChar.sendPacket(SystemMsg.INVALID_TARGET);
						return false;
					}
					if(target.getPlayer().getLevel() < 10)
					{
						activeChar.sendPacket(SystemMsg.YOU_DO_NOT_MEET_THE_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN);
						return false;
					}
					String pledgeName = st.nextToken();
					if(pledgeName.length() > 16)
					{
						activeChar.sendPacket(SystemMsg.CLAN_NAMES_LENGTH_IS_INCORRECT);
						return false;
					}
					if(!Util.isMatchingRegexp(pledgeName, Config.CLAN_NAME_TEMPLATE))
					{
						// clan name is not matching template
						activeChar.sendPacket(SystemMsg.CLAN_NAME_IS_INVALID);
						return false;
					}

					Clan clan = ClanTable.getInstance().createClan(target, pledgeName);
					if(clan != null)
					{
						target.sendPacket(clan.listAll());
						target.sendPacket(new PledgeShowInfoUpdatePacket(clan), SystemMsg.YOUR_CLAN_HAS_BEEN_CREATED);
						target.updatePledgeRank();
						target.sendUserInfo(true);
						return true;
					}
					else
					{
						activeChar.sendPacket(SystemMsg.THIS_NAME_ALREADY_EXISTS);
						return false;
					}
				}
				catch(Exception e)
				{}
			else if(action.equals("setlevel"))
			{
				if(target.getClan() == null)
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
					return false;
				}

				try
				{
					int level = Integer.parseInt(st.nextToken());
					Clan clan = target.getClan();

					activeChar.sendMessage("You set level " + level + " for clan " + clan.getName());
					int oldLevel = clan.getLevel();
					clan.setLevel(level);
					clan.updateClanInDB();

					clan.onLevelChange(oldLevel, clan.getLevel());

					return true;
				}
				catch(Exception e)
				{}
			}
			else if(action.equals("resetcreate"))
			{
				if(target.getClan() == null)
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
					return false;
				}
				target.getClan().setExpelledMemberTime(0);
				activeChar.sendMessage("The penalty for creating a clan has been lifted for " + target.getName());
			}
			else if(action.equals("resetwait"))
			{
				target.setLeaveClanTime(0);
				activeChar.sendMessage("The penalty for leaving a clan has been lifted for " + target.getName());
			}
			else if(action.equals("addrep"))
				try
				{
					int rep = Integer.parseInt(st.nextToken());

					if(target.getClan() == null || target.getClan().getLevel() < 3)
					{
						activeChar.sendPacket(SystemMsg.INVALID_TARGET);
						return false;
					}
					target.getClan().incReputation(rep, false, "admin_manual");
					activeChar.sendMessage("Added " + rep + " clan points to clan " + target.getClan().getName() + ".");
				}
				catch(NumberFormatException nfe)
				{
					activeChar.sendMessage("Please specify a number of clan points to add.");
				}
			else if(action.equals("setleader"))
			{
				Clan clan = target.getClan();
				if(target.getClan() == null)
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
					return false;
				}
				String newLeaderName = null;
				if(st.hasMoreTokens())
					newLeaderName = st.nextToken();
				else
					newLeaderName = target.getName();
				SubUnit mainUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
				UnitMember newLeader = mainUnit.getUnitMember(newLeaderName);
				if(newLeader == null)
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
					return false;
				}
				VillageMasterInstance.setLeader(activeChar, clan, mainUnit, newLeader);
			}
		}

		return false;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}