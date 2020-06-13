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

import org.l2j.commons.util.Util;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.model.html.formatters.BypassParserFormatter;
import org.l2j.gameserver.model.html.pagehandlers.NextPrevPageHandler;
import org.l2j.gameserver.model.html.styles.ButtonsStyle;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.BypassParser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Instance admin commands.
 * @author St3eT
 */
public final class AdminInstance implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_instance",
                    "admin_instances",
                    "admin_instancelist",
                    "admin_instancecreate",
                    "admin_instanceteleport",
                    "admin_instancedestroy",
            };
    private static final int[] IGNORED_TEMPLATES =
            {
                    127, // Chamber of Delusion
                    128, // Chamber of Delusion
                    129, // Chamber of Delusion
                    130, // Chamber of Delusion
                    131, // Chamber of Delusion
                    132, // Chamber of Delusion
                    147, // Grassy Arena
                    149, // Heros's Vestiges Arena
                    150, // Orbis Arena
                    148, // Three Bridges Arena
            };

    @Override
    public boolean useAdminCommand(String command, Player activeChar)
    {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();

        switch (actualCommand.toLowerCase())
        {
            case "admin_instance":
            case "admin_instances":
            {
                final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
                html.setFile(activeChar, "data/html/admin/instances.htm");
                html.replace("%instCount%", InstanceManager.getInstance().getInstances().size());
                html.replace("%tempCount%", InstanceManager.getInstance().getInstanceTemplates().size());
                activeChar.sendPacket(html);
                break;
            }
            case "admin_instancelist":
            {
                processBypass(activeChar, new BypassParser(command));
                break;
            }
            case "admin_instancecreate":
            {
                final int templateId = Util.parseNextInt(st, 0);
                final InstanceTemplate template = InstanceManager.getInstance().getInstanceTemplate(templateId);

                if (template != null)
                {
                    final String enterGroup = st.hasMoreTokens() ? st.nextToken() : "Alone";
                    final List<Player> members = new ArrayList<>();

                    switch (enterGroup)
                    {
                        case "Alone":
                        {
                            members.add(activeChar);
                            break;
                        }
                        case "Party":
                        {
                            if (activeChar.isInParty())
                            {
                                members.addAll(activeChar.getParty().getMembers());
                            }
                            else
                            {
                                members.add(activeChar);
                            }
                            break;
                        }
                        case "CommandChannel":
                        {
                            if (activeChar.isInCommandChannel())
                            {
                                members.addAll(activeChar.getParty().getCommandChannel().getMembers());
                            }
                            else if (activeChar.isInParty())
                            {
                                members.addAll(activeChar.getParty().getMembers());
                            }
                            else
                            {
                                members.add(activeChar);
                            }
                            break;
                        }
                        default:
                        {
                            BuilderUtil.sendSysMessage(activeChar, "Wrong enter group usage! Please use those values: Alone, Party or CommandChannel.");
                            return true;
                        }
                    }

                    final Instance instance = InstanceManager.getInstance().createInstance(template, activeChar);
                    final Location loc = instance.getEnterLocation();
                    if (loc != null)
                    {
                        for (Player players : members)
                        {
                            instance.addAllowed(players);
                            players.teleToLocation(loc, instance);
                        }
                    }
                    sendTemplateDetails(activeChar, instance.getTemplateId());
                }
                else
                {
                    BuilderUtil.sendSysMessage(activeChar, "Wrong parameters! Please try again.");
                    return true;
                }
                break;
            }
            case "admin_instanceteleport":
            {
                final Instance instance = InstanceManager.getInstance().getInstance(Util.parseNextInt(st, -1));
                if (instance != null)
                {
                    final Location loc = instance.getEnterLocation();
                    if (loc != null)
                    {
                        if (!instance.isAllowed(activeChar))
                        {
                            instance.addAllowed(activeChar);
                        }
                        activeChar.teleToLocation(loc, false);
                        activeChar.setInstance(instance);
                        sendTemplateDetails(activeChar, instance.getTemplateId());
                    }
                }
                break;
            }
            case "admin_instancedestroy":
            {
                final Instance instance = InstanceManager.getInstance().getInstance(Util.parseNextInt(st, -1));
                if (instance != null)
                {
                    instance.getPlayers().forEach(player -> player.sendPacket(new ExShowScreenMessage("Your instance has been destroyed by Game Master!", 10000)));
                    BuilderUtil.sendSysMessage(activeChar, "You destroyed Instance " + instance.getId() + " with " + instance.getPlayersCount() + " players inside.");
                    instance.destroy();
                    sendTemplateDetails(activeChar, instance.getTemplateId());
                }
                break;
            }
        }
        return true;
    }

    private void sendTemplateDetails(Player player, int templateId)
    {
        if (InstanceManager.getInstance().getInstanceTemplate(templateId) != null)
        {
            final InstanceTemplate template = InstanceManager.getInstance().getInstanceTemplate(templateId);
            final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
            final StringBuilder sb = new StringBuilder();
            html.setFile(player, "data/html/admin/instances_detail.htm");
            html.replace("%templateId%", template.getId());
            html.replace("%templateName%", template.getName());
            html.replace("%activeWorlds%", template.getWorldCount() + " / " + (template.getMaxWorlds() == -1 ? "Unlimited" : template.getMaxWorlds()));
            html.replace("%duration%", template.getDuration() + " minutes");
            html.replace("%emptyDuration%", TimeUnit.MILLISECONDS.toMinutes(template.getEmptyDestroyTime()) + " minutes");
            html.replace("%ejectDuration%", template.getEjectTime() + " minutes");
            html.replace("%removeBuff%", template.isRemoveBuffEnabled());

            sb.append("<table border=0 cellpadding=2 cellspacing=0 bgcolor=\"363636\">");
            sb.append("<tr>");
            sb.append("<td fixwidth=\"83\"><font color=\"LEVEL\">Instance ID</font></td>");
            sb.append("<td fixwidth=\"83\"><font color=\"LEVEL\">Teleport</font></td>");
            sb.append("<td fixwidth=\"83\"><font color=\"LEVEL\">Destroy</font></td>");
            sb.append("</tr>");
            sb.append("</table>");

            InstanceManager.getInstance().getInstances().stream().filter(inst -> (inst.getTemplateId() == templateId)).sorted(Comparator.comparingInt(Instance::getPlayersCount)).forEach(instance ->
            {
                sb.append("<table border=0 cellpadding=2 cellspacing=0 bgcolor=\"363636\">");
                sb.append("<tr>");
                sb.append("<td fixwidth=\"83\">" + instance.getId() + "</td>");
                sb.append("<td fixwidth=\"83\"><button value=\"Teleport!\" action=\"bypass -h admin_instanceteleport " + instance.getId() + "\" width=75 height=18 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
                sb.append("<td fixwidth=\"83\"><button value=\"Destroy!\" action=\"bypass -h admin_instancedestroy " + instance.getId() + "\" width=75 height=18 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
                sb.append("</tr>");
                sb.append("</table>");
            });

            html.replace("%instanceList%", sb.toString());
            player.sendPacket(html);
        }
        else
        {
            player.sendMessage("Instance template with id " + templateId + " does not exist!");
            useAdminCommand("admin_instance", player);
        }
    }

    private void sendTemplateList(Player player, int page, BypassParser parser)
    {
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(player, "data/html/admin/instances_list.htm");

        final InstanceManager instManager = InstanceManager.getInstance();
        final List<InstanceTemplate> templateList = instManager.getInstanceTemplates().stream().sorted(Comparator.comparingLong(InstanceTemplate::getWorldCount).reversed()).filter(template -> !Util.contains(IGNORED_TEMPLATES, template.getId())).collect(Collectors.toList());

        //@formatter:off
        final PageResult result = PageBuilder.newBuilder(templateList, 4, "bypass -h admin_instancelist")
                .currentPage(page)
                .pageHandler(NextPrevPageHandler.INSTANCE)
                .formatter(BypassParserFormatter.INSTANCE)
                .style(ButtonsStyle.INSTANCE)
                .bodyHandler((pages, template, sb) ->
                {
                    sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
                    sb.append("<tr><td align=center fixwidth=\"250\"><font color=\"LEVEL\">" + template.getName() + " (" + template.getId() + ")</font></td></tr>");
                    sb.append("</table>");

                    sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
                    sb.append("<tr>");
                    sb.append("<td align=center fixwidth=\"83\">Active worlds:</td>");
                    sb.append("<td align=center fixwidth=\"83\"></td>");
                    sb.append("<td align=center fixwidth=\"83\">" + template.getWorldCount() + " / " + (template.getMaxWorlds() == -1 ? "Unlimited" : template.getMaxWorlds()) + "</td>");
                    sb.append("</tr>");

                    sb.append("<tr>");
                    sb.append("<td align=center fixwidth=\"83\">Detailed info:</td>");
                    sb.append("<td align=center fixwidth=\"83\"></td>");
                    sb.append("<td align=center fixwidth=\"83\"><button value=\"Show me!\" action=\"bypass -h admin_instancelist id=" + template.getId() + "\" width=\"85\" height=\"20\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    sb.append("</tr>");


                    sb.append("</table>");
                    sb.append("<br>");
                }).build();
        //@formatter:on

        html.replace("%pages%", result.getPages() > 0 ? "<center><table width=\"100%\" cellspacing=0><tr>" + result.getPagerTemplate() + "</tr></table></center>" : "");
        html.replace("%data%", result.getBodyTemplate().toString());
        player.sendPacket(html);
    }

    private void processBypass(Player player, BypassParser parser)
    {
        final int page = parser.getInt("page", 0);
        final int templateId = parser.getInt("id", 0);

        if (templateId > 0)
        {
            sendTemplateDetails(player, templateId);

        }
        else
        {
            sendTemplateList(player, page, parser);
        }
    }

    @Override
    public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
    }
}