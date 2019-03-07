/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jmobius.gameserver.data.sql.impl.ClanTable;
import com.l2jmobius.gameserver.enums.UserInfoType;
import com.l2jmobius.gameserver.handler.IAdminCommandHandler;
import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.L2Object;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.GMViewPledgeInfo;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import com.l2jmobius.gameserver.util.BuilderUtil;

/**
 * <B>Pledge Manipulation:</B><BR>
 * <LI>With target in a character without clan:<BR>
 * //pledge create clanname
 * <LI>With target in a clan leader:<BR>
 * //pledge info<BR>
 * //pledge dismiss<BR>
 * //pledge setlevel level<BR>
 * //pledge rep reputation_points<BR>
 */
public class AdminPledge implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_pledge"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command);
		final String cmd = st.nextToken();
		final L2Object target = activeChar.getTarget();
		final L2PcInstance targetPlayer = (target != null) && target.isPlayer() ? (L2PcInstance) target : null;
		L2Clan clan = targetPlayer != null ? targetPlayer.getClan() : null;
		if (targetPlayer == null)
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			showMainPage(activeChar);
			return false;
		}
		switch (cmd)
		{
			case "admin_pledge":
			{
				if (!st.hasMoreTokens())
				{
					BuilderUtil.sendSysMessage(activeChar, "Missing parameters!");
					break;
				}
				final String action = st.nextToken();
				if (!st.hasMoreTokens())
				{
					BuilderUtil.sendSysMessage(activeChar, "Missing parameters!");
					break;
				}
				final String param = st.nextToken();
				
				switch (action)
				{
					case "create":
					{
						if (clan != null)
						{
							BuilderUtil.sendSysMessage(activeChar, "Target player has clan!");
							break;
						}
						
						final long penalty = targetPlayer.getClanCreateExpiryTime();
						targetPlayer.setClanCreateExpiryTime(0);
						clan = ClanTable.getInstance().createClan(targetPlayer, param);
						if (clan != null)
						{
							BuilderUtil.sendSysMessage(activeChar, "Clan " + param + " created. Leader: " + targetPlayer.getName());
						}
						else
						{
							targetPlayer.setClanCreateExpiryTime(penalty);
							BuilderUtil.sendSysMessage(activeChar, "There was a problem while creating the clan.");
						}
						break;
					}
					case "dismiss":
					{
						if (clan == null)
						{
							BuilderUtil.sendSysMessage(activeChar, "Target player has no clan!");
							break;
						}
						
						if (!targetPlayer.isClanLeader())
						{
							final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER);
							sm.addString(targetPlayer.getName());
							activeChar.sendPacket(sm);
							showMainPage(activeChar);
							return false;
						}
						
						ClanTable.getInstance().destroyClan(targetPlayer.getClanId());
						clan = targetPlayer.getClan();
						if (clan == null)
						{
							BuilderUtil.sendSysMessage(activeChar, "Clan disbanded.");
						}
						else
						{
							BuilderUtil.sendSysMessage(activeChar, "There was a problem while destroying the clan.");
						}
						break;
					}
					case "info":
					{
						if (clan == null)
						{
							BuilderUtil.sendSysMessage(activeChar, "Target player has no clan!");
							break;
						}
						
						activeChar.sendPacket(new GMViewPledgeInfo(clan, targetPlayer));
						break;
					}
					case "setlevel":
					{
						if (clan == null)
						{
							BuilderUtil.sendSysMessage(activeChar, "Target player has no clan!");
							break;
						}
						else if (param == null)
						{
							BuilderUtil.sendSysMessage(activeChar, "Usage: //pledge <setlevel|rep> <number>");
							break;
						}
						
						final int level = Integer.parseInt(param);
						if ((level >= 0) && (level < 12))
						{
							clan.changeLevel(level);
							for (L2PcInstance member : clan.getOnlineMembers(0))
							{
								member.broadcastUserInfo(UserInfoType.RELATION, UserInfoType.CLAN);
							}
							BuilderUtil.sendSysMessage(activeChar, "You set level " + level + " for clan " + clan.getName());
						}
						else
						{
							BuilderUtil.sendSysMessage(activeChar, "Level incorrect.");
						}
						break;
					}
					case "rep":
					{
						if (clan == null)
						{
							BuilderUtil.sendSysMessage(activeChar, "Target player has no clan!");
							break;
						}
						else if (clan.getLevel() < 5)
						{
							BuilderUtil.sendSysMessage(activeChar, "Only clans of level 5 or above may receive reputation points.");
							showMainPage(activeChar);
							return false;
						}
						
						try
						{
							final int points = Integer.parseInt(param);
							clan.addReputationScore(points, true);
							BuilderUtil.sendSysMessage(activeChar, "You " + (points > 0 ? "add " : "remove ") + Math.abs(points) + " points " + (points > 0 ? "to " : "from ") + clan.getName() + "'s reputation. Their current score is " + clan.getReputationScore());
						}
						catch (Exception e)
						{
							BuilderUtil.sendSysMessage(activeChar, "Usage: //pledge <rep> <number>");
						}
						break;
					}
				}
				break;
			}
		}
		showMainPage(activeChar);
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void showMainPage(L2PcInstance activeChar)
	{
		AdminHtml.showAdminHtml(activeChar, "game_menu.htm");
	}
	
}
