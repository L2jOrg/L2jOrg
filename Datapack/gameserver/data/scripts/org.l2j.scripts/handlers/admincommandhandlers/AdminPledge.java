/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.GMViewPledgeInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

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
    public boolean useAdminCommand(String command, Player activeChar)
    {
        final StringTokenizer st = new StringTokenizer(command);
        final String cmd = st.nextToken();
        final WorldObject target = activeChar.getTarget();
        final Player targetPlayer = isPlayer(target) ? (Player) target : null;
        Clan clan = targetPlayer != null ? targetPlayer.getClan() : null;
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
                        if (clan == null) {
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

                        ClanTable.getInstance().destroyClan(clan);
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
                            for (Player member : clan.getOnlineMembers(0))
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

    private void showMainPage(Player activeChar)
    {
        AdminHtml.showAdminHtml(activeChar, "game_menu.htm");
    }

}
