/*
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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.datatables.SchemeBufferTable;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;

import java.text.NumberFormat;
import java.util.*;

import static org.l2j.gameserver.network.SystemMessageId.YOU_DO_NOT_HAVE_A_PET;

public class SchemeBuffer extends Folk {
    private static final int PAGE_LIMIT = 6;

    public SchemeBuffer(NpcTemplate template) {
        super(template);
    }

    /**
     * @param groupType  : The group of skills to select.
     * @param schemeName : The scheme to make check.
     * @return a string representing all groupTypes available. The group currently on selection isn't linkable.
     */
    private static String getTypesFrame(String groupType, String schemeName) {
        final StringBuilder sb = new StringBuilder(500);
        sb.append("<table>");

        int count = 0;
        for (String type : SchemeBufferTable.getInstance().getSkillTypes()) {
            if (count == 0) {
                sb.append("<tr>");
            }

            if (groupType.equalsIgnoreCase(type)) {
                sb.append("<td width=65>").append(type).append("</td>");
            } else {
                sb.append("<td width=65><a action=\"bypass -h npc_%objectId%_editschemes;").append(type).append(";").append(schemeName).append(";1\">").append(type).append("</a></td>");
            }

            count++;
            if (count == 4) {
                sb.append("</tr>");
                count = 0;
            }
        }

        if (!sb.toString().endsWith("</tr>")) {
            sb.append("</tr>");
        }

        sb.append("</table>");

        return sb.toString();
    }

    /**
     * @param list : A list of skill ids.
     * @return a global fee for all skills contained in list.
     */
    private static int getFee(ArrayList<Integer> list) {
        if (Config.BUFFER_STATIC_BUFF_COST > 0) {
            return list.size() * Config.BUFFER_STATIC_BUFF_COST;
        }

        int fee = 0;
        for (int sk : list) {
            fee += SchemeBufferTable.getInstance().getAvailableBuff(sk).getPrice();
        }

        return fee;
    }

    private static int countPagesNumber(int objectsSize, int pageSize) {
        return (objectsSize / pageSize) + ((objectsSize % pageSize) == 0 ? 0 : 1);
    }

    private static long getCountOf(List<Integer> skills, boolean dances) {
        return skills.stream().filter(sId -> SkillEngine.getInstance().getSkill(sId, 1).isDance() == dances).count();
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        command = command.replace("createscheme ", "createscheme;");
        StringTokenizer st = new StringTokenizer(command, ";");
        String currentCommand = st.nextToken();

        if (currentCommand.startsWith("menu")) {
            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile(player, getHtmlPath(getId(), 0));
            html.replace("%objectId%", getObjectId());
            player.sendPacket(html);
        } else if (currentCommand.startsWith("cleanup")) {
            player.stopAllEffects();

            final Summon summon = player.getPet();
            if (summon != null) {
                summon.stopAllEffects();
            }

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile(player, getHtmlPath(getId(), 0));
            html.replace("%objectId%", getObjectId());
            player.sendPacket(html);
        } else if (currentCommand.startsWith("heal")) {
            player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());

            final Summon summon = player.getPet();
            if (summon != null) {
                summon.setCurrentHpMp(summon.getMaxHp(), summon.getMaxMp());
            }

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile(player, getHtmlPath(getId(), 0));
            html.replace("%objectId%", getObjectId());
            player.sendPacket(html);
        } else if (currentCommand.startsWith("support")) {
            showGiveBuffsWindow(player);
        } else if (currentCommand.startsWith("givebuffs")) {
            final String schemeName = st.nextToken();
            final int cost = Integer.parseInt(st.nextToken());

            Creature target = null;
            if (st.hasMoreTokens()) {
                final String targetType = st.nextToken();
                if ((targetType != null) && targetType.equalsIgnoreCase("pet")) {
                    target = player.getPet();
                }
            } else {
                target = player;
            }

            if (target == null) {
                player.sendPacket(YOU_DO_NOT_HAVE_A_PET);
            } else if ((cost == 0) || player.reduceAdena("NPC Buffer", cost, this, true)) {
                for (int skillId : SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName)) {
                    SkillEngine.getInstance().getSkill(skillId, SkillEngine.getInstance().getMaxLevel(skillId)).applyEffects(this, target);
                }
            }
        } else if (currentCommand.startsWith("editschemes")) {
            showEditSchemeWindow(player, st.nextToken(), st.nextToken(), Integer.parseInt(st.nextToken()));
        } else if (currentCommand.startsWith("skill")) {
            final String groupType = st.nextToken();
            final String schemeName = st.nextToken();

            final int skillId = Integer.parseInt(st.nextToken());
            final int page = Integer.parseInt(st.nextToken());

            final List<Integer> skills = SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName);

            if (currentCommand.startsWith("skillselect") && !schemeName.equalsIgnoreCase("none")) {
                final Skill skill = SkillEngine.getInstance().getSkill(skillId, SkillEngine.getInstance().getMaxLevel(skillId));
                if (skill.isDance()) {
                    if (getCountOf(skills, true) < Config.DANCES_MAX_AMOUNT) {
                        skills.add(skillId);
                    } else {
                        player.sendMessage("This scheme has reached the maximum amount of dances/songs.");
                    }
                } else {
                    if (getCountOf(skills, false) < player.getStats().getMaxBuffCount()) {
                        skills.add(skillId);
                    } else {
                        player.sendMessage("This scheme has reached the maximum amount of buffs.");
                    }
                }
            } else if (currentCommand.startsWith("skillunselect")) {
                skills.remove(Integer.valueOf(skillId));
            }

            showEditSchemeWindow(player, groupType, schemeName, page);
        } else if (currentCommand.startsWith("createscheme")) {
            try {
                final String schemeName = st.nextToken().trim();
                if (schemeName.length() > 14) {
                    player.sendMessage("Scheme's name must contain up to 14 chars.");
                    return;
                }

                if (!Util.isAlphaNumeric(schemeName.replace(" ", "").replace(".", "").replace(",", "").replace("-", "").replace("+", "").replace("!", "").replace("?", ""))) {
                    player.sendMessage("Please use plain alphanumeric characters.");
                    return;
                }

                final Map<String, ArrayList<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(player.getObjectId());
                if (schemes != null) {
                    if (schemes.size() == Config.BUFFER_MAX_SCHEMES) {
                        player.sendMessage("Maximum schemes amount is already reached.");
                        return;
                    }

                    if (schemes.containsKey(schemeName)) {
                        player.sendMessage("The scheme name already exists.");
                        return;
                    }
                }

                SchemeBufferTable.getInstance().setScheme(player.getObjectId(), schemeName.trim(), new ArrayList<>());
                showGiveBuffsWindow(player);
            } catch (Exception e) {
                player.sendMessage("Scheme's name must contain up to 14 chars.");
            }
        } else if (currentCommand.startsWith("deletescheme")) {
            try {
                final String schemeName = st.nextToken();
                final Map<String, ArrayList<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(player.getObjectId());

                if ((schemes != null) && schemes.containsKey(schemeName)) {
                    schemes.remove(schemeName);
                }
            } catch (Exception e) {
                player.sendMessage("This scheme name is invalid.");
            }
            showGiveBuffsWindow(player);
        }
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String filename = "";
        if (val == 0) {
            filename = Integer.toString(npcId);
        } else {
            filename = npcId + "-" + val;
        }

        return "data/html/mods/SchemeBuffer/" + filename + ".htm";
    }

    /**
     * Sends an html packet to player with Give Buffs menu info for player and pet, depending on targetType parameter {player, pet}
     *
     * @param player : The player to make checks on.
     */
    private void showGiveBuffsWindow(Player player) {
        final StringBuilder sb = new StringBuilder(200);

        final Map<String, ArrayList<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(player.getObjectId());
        if ((schemes == null) || schemes.isEmpty()) {
            sb.append("<font color=\"LEVEL\">You haven't defined any scheme.</font>");
        } else {
            for (Map.Entry<String, ArrayList<Integer>> scheme : schemes.entrySet()) {
                final int cost = getFee(scheme.getValue());
                sb.append("<font color=\"LEVEL\">" + scheme.getKey() + " [" + scheme.getValue().size() + " skill(s)]" + ((cost > 0) ? " - cost: " + NumberFormat.getInstance(Locale.ENGLISH).format(cost) : "") + "</font><br1>");
                sb.append("<a action=\"bypass -h npc_%objectId%_givebuffs;" + scheme.getKey() + ";" + cost + "\">Use on Me</a>&nbsp;|&nbsp;");
                sb.append("<a action=\"bypass -h npc_%objectId%_givebuffs;" + scheme.getKey() + ";" + cost + ";pet\">Use on Pet</a>&nbsp;|&nbsp;");
                sb.append("<a action=\"bypass -h npc_%objectId%_editschemes;Buffs;" + scheme.getKey() + ";1\">Edit</a>&nbsp;|&nbsp;");
                sb.append("<a action=\"bypass -h npc_%objectId%_deletescheme;" + scheme.getKey() + "\">Delete</a><br>");
            }
        }

        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(player, getHtmlPath(getId(), 1));
        html.replace("%schemes%", sb.toString());
        html.replace("%max_schemes%", Config.BUFFER_MAX_SCHEMES);
        html.replace("%objectId%", getObjectId());
        player.sendPacket(html);
    }

    /**
     * This sends an html packet to player with Edit Scheme Menu info. This allows player to edit each created scheme (add/delete skills)
     *
     * @param player     : The player to make checks on.
     * @param groupType  : The group of skills to select.
     * @param schemeName : The scheme to make check.
     * @param page       The page.
     */
    private void showEditSchemeWindow(Player player, String groupType, String schemeName, int page) {
        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        final List<Integer> schemeSkills = SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName);

        html.setFile(player, getHtmlPath(getId(), 2));
        html.replace("%schemename%", schemeName);
        html.replace("%count%", getCountOf(schemeSkills, false) + " / " + player.getStats().getMaxBuffCount() + " buffs, " + getCountOf(schemeSkills, true) + " / " + Config.DANCES_MAX_AMOUNT + " dances/songs");
        html.replace("%typesframe%", getTypesFrame(groupType, schemeName));
        html.replace("%skilllistframe%", getGroupSkillList(player, groupType, schemeName, page));
        html.replace("%objectId%", getObjectId());
        player.sendPacket(html);
    }

    /**
     * @param player     : The player to make checks on.
     * @param groupType  : The group of skills to select.
     * @param schemeName : The scheme to make check.
     * @param page       The page.
     * @return a String representing skills available to selection for a given groupType.
     */
    private String getGroupSkillList(Player player, String groupType, String schemeName, int page) {
        // Retrieve the entire skills list based on group type.
        List<Integer> skills = SchemeBufferTable.getInstance().getSkillsIdsByType(groupType);
        if (skills.isEmpty()) {
            return "That group doesn't contain any skills.";
        }

        // Calculate page number.
        final int max = countPagesNumber(skills.size(), PAGE_LIMIT);
        if (page > max) {
            page = max;
        }

        // Cut skills list up to page number.
        skills = skills.subList((page - 1) * PAGE_LIMIT, Math.min(page * PAGE_LIMIT, skills.size()));

        final List<Integer> schemeSkills = SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
        final StringBuilder sb = new StringBuilder(skills.size() * 150);

        int row = 0;
        for (int skillId : skills) {
            final Skill skill = SkillEngine.getInstance().getSkill(skillId, 1);
            sb.append(row++ % 2 == 0 ? "<table width=\"280\" bgcolor=\"000000\"><tr>" : "<table width=\"280\"><tr>")
                .append("<td height=40 width=40><img src=\"").append(skill.getIcon()).append("\" width=32 height=32></td><td width=190>").append(skill.getName())
                .append("<br1><font color=\"B09878\">").append(SchemeBufferTable.getInstance().getAvailableBuff(skillId).getDescription())

                .append("</font></td><td><button value=\" \" action=\"bypass -h npc_%objectId%_").append(schemeSkills.contains(skillId) ? "skillunselect;" :"skillselect;")

                .append(groupType).append(";").append(schemeName).append(";").append(skillId).append(";").append(page)
                .append("\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>")
                .append("</tr></table><img src=\"L2UI.SquareGray\" width=277 height=1>");
        }

        // Build page footer.
        sb.append("<br><img src=\"L2UI.SquareGray\" width=277 height=1><table width=\"100%\" bgcolor=000000><tr>");

        if (page > 1) {
            sb.append("<td align=left width=70><a action=\"bypass -h npc_").append(getObjectId()).append("_editschemes;").append(groupType).append(";").append(schemeName).append(";").append(page - 1).append("\">Previous</a></td>");
        } else {
            sb.append("<td align=left width=70>Previous</td>");
        }

        sb.append("<td align=center width=100>Page ").append(page).append("</td>");

        if (page < max) {
            sb.append("<td align=right width=70><a action=\"bypass -h npc_").append(getObjectId()).append("_editschemes;").append(groupType).append(";").append(schemeName).append(";").append(page + 1).append("\">Next</a></td>");
        } else {
            sb.append("<td align=right width=70>Next</td>");
        }

        sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=277 height=1>");

        return sb.toString();
    }
}