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
package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SellBuffHolder;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreSetWholeMsg;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.util.HtmlUtil;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Sell Buffs Manager
 *
 * @author St3eT
 *
 */
public final class SellBuffsManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SellBuffsManager.class);
    private static final List<Integer> ALLOWED_BUFFS = new ArrayList<>();
    private static final String htmlFolder = "data/html/mods/SellBuffs/";

    private SellBuffsManager() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/SellBuffData.xsd");
    }

    @Override
    public void load() {
        if (Config.SELLBUFF_ENABLED) {
            ALLOWED_BUFFS.clear();
            parseDatapackFile("data/SellBuffData.xml");
            LOGGER.info("Loaded {} allowed buffs.", ALLOWED_BUFFS.size());
        }
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        final NodeList node = doc.getDocumentElement().getElementsByTagName("skill");
        for (int i = 0; i < node.getLength(); ++i) {
            final Element elem = (Element) node.item(i);
            final int skillId = Integer.parseInt(elem.getAttribute("id"));

            if (!ALLOWED_BUFFS.contains(skillId)) {
                ALLOWED_BUFFS.add(skillId);
            }
        }
    }

    public void sendSellMenu(Player player) {
        final String html = HtmCache.getInstance().getHtm(player, htmlFolder + (player.isSellingBuffs() ? "BuffMenu_already.html" : "BuffMenu.html"));
        CommunityBoardHandler.separateAndSend(html, player);
    }

    public void sendBuffChoiceMenu(Player player, int index) {
        String html = HtmCache.getInstance().getHtm(player, htmlFolder + "BuffChoice.html");
        html = html.replace("%list%", buildSkillMenu(player, index));
        CommunityBoardHandler.separateAndSend(html, player);
    }

    public void sendBuffEditMenu(Player player) {
        String html = HtmCache.getInstance().getHtm(player, htmlFolder + "BuffChoice.html");
        html = html.replace("%list%", buildEditMenu(player));
        CommunityBoardHandler.separateAndSend(html, player);
    }

    public void sendBuffMenu(Player player, Player seller, int index) {
        if (!seller.isSellingBuffs() || seller.getSellingBuffs().isEmpty()) {
            return;
        }

        String html = HtmCache.getInstance().getHtm(player, htmlFolder + "BuffBuyMenu.html");
        html = html.replace("%list%", buildBuffMenu(player, seller, index));
        CommunityBoardHandler.separateAndSend(html, player);
    }

    public void startSellBuffs(Player player, String title) {
        player.sitDown();
        player.setIsSellingBuffs(true);
        player.setPrivateStoreType(PrivateStoreType.PACKAGE_SELL);
        player.getSellList().setTitle(title);
        player.getSellList().setPackaged(true);
        player.broadcastUserInfo();
        player.broadcastPacket(new ExPrivateStoreSetWholeMsg(player));
        sendSellMenu(player);
    }

    public void stopSellBuffs(Player player) {
        player.setIsSellingBuffs(false);
        player.setPrivateStoreType(PrivateStoreType.NONE);
        player.standUp();
        player.broadcastUserInfo();
        sendSellMenu(player);
    }

    private String buildBuffMenu(Player player, Player seller, int index) {
        final int ceiling = 10;
        int nextIndex = -1;
        int previousIndex = -1;
        int emptyFields = 0;
        final StringBuilder sb = new StringBuilder();
        final List<SellBuffHolder> sellList = new ArrayList<>();

        int count = 0;
        for (SellBuffHolder holder : seller.getSellingBuffs()) {
            count++;
            if ((count > index) && (count <= (ceiling + index))) {
                sellList.add(holder);
            }
        }

        if (count > 10) {
            if (count > (index + 10)) {
                nextIndex = index + 10;
            }
        }

        if (index >= 10) {
            previousIndex = index - 10;
        }

        emptyFields = ceiling - sellList.size();

        sb.append("<br>");
        sb.append(HtmlUtil.getMpGauge(250, (long) seller.getCurrentMp(), seller.getMaxMp(), false));
        sb.append("<br>");

        sb.append("<table border=0 cellpadding=0 cellspacing=0 background=\"L2UI_CH3.refinewnd_back_Pattern\">");
        sb.append("<tr><td><br><br><br></td></tr>");
        sb.append("<tr>");
        sb.append("<td fixwidth=\"10\"></td>");
        sb.append("<td> <button action=\"\" value=\"Icon\" width=75 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Icon
        sb.append("<td> <button action=\"\" value=\"Name\" width=175 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Name
        sb.append("<td> <button action=\"\" value=\"Level\" width=85 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Leve
        sb.append("<td> <button action=\"\" value=\"MP Cost\" width=100 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Price
        sb.append("<td> <button action=\"\" value=\"Price\" width=200 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Price
        sb.append("<td> <button action=\"\" value=\"Action\" width=100 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Action
        sb.append("<td fixwidth=\"20\"></td>");
        sb.append("</tr>");

        for (SellBuffHolder holder : sellList) {
            final Skill skill = seller.getKnownSkill(holder.getSkillId());
            if (skill == null) {
                emptyFields++;
                continue;
            }

            final ItemTemplate item = ItemEngine.getInstance().getTemplate(Config.SELLBUFF_PAYMENT_ID);

            sb.append("<tr>");
            sb.append("<td fixwidth=\"20\"></td>");
            sb.append("<td align=center><img src=\"" + skill.getIcon() + "\" width=\"32\" height=\"32\"></td>");
            sb.append("<td align=left>" + skill.getName() + (skill.getLevel() > 100 ? "<font color=\"LEVEL\"> + " + (skill.getLevel() % 100) + "</font></td>" : "</td>"));
            sb.append("<td align=center>" + ((skill.getLevel() > 100) ? SkillEngine.getInstance().getMaxLevel(skill.getId()) : skill.getLevel()) + "</td>");
            sb.append("<td align=center> <font color=\"1E90FF\">" + (skill.getMpConsume() * Config.SELLBUFF_MP_MULTIPLER) + "</font></td>");
            sb.append("<td align=center> " + GameUtils.formatAdena(holder.getPrice()) + " <font color=\"LEVEL\"> " + (item != null ? item.getName() : "") + "</font> </td>");
            sb.append("<td align=center fixwidth=\"50\"><button value=\"Buy Buff\" action=\"bypass -h sellbuffbuyskill " + seller.getObjectId() + " " + skill.getId() + " " + index + "\" width=\"85\" height=\"26\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
            sb.append("</tr>");
            sb.append("<tr><td><br><br></td></tr>");
        }

        for (int i = 0; i < emptyFields; i++) {
            sb.append("<tr>");
            sb.append("<td fixwidth=\"20\" height=\"32\"></td>");
            sb.append("<td align=center></td>");
            sb.append("<td align=left></td>");
            sb.append("<td align=center></td>");
            sb.append("<td align=center></font></td>");
            sb.append("<td align=center></td>");
            sb.append("<td align=center fixwidth=\"50\"></td>");
            sb.append("</tr>");
            sb.append("<tr><td><br><br></td></tr>");
        }

        sb.append("</table>");

        sb.append("<table width=\"250\" border=\"0\">");
        sb.append("<tr>");

        if (previousIndex > -1) {
            sb.append("<td align=left><button value=\"Previous Page\" action=\"bypass -h sellbuffbuymenu " + seller.getObjectId() + " " + previousIndex + "\" width=\"100\" height=\"30\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        }

        if (nextIndex > -1) {
            sb.append("<td align=right><button value=\"Next Page\" action=\"bypass -h sellbuffbuymenu " + seller.getObjectId() + " " + nextIndex + "\" width=\"100\" height=\"30\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        }
        sb.append("</tr>");
        sb.append("</table>");
        return sb.toString();
    }

    private String buildEditMenu(Player player) {
        final StringBuilder sb = new StringBuilder();

        sb.append("<table border=0 cellpadding=0 cellspacing=0 background=\"L2UI_CH3.refinewnd_back_Pattern\">");
        sb.append("<tr><td><br><br><br></td></tr>");
        sb.append("<tr>");
        sb.append("<td fixwidth=\"10\"></td>");
        sb.append("<td> <button action=\"\" value=\"Icon\" width=75 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Icon
        sb.append("<td> <button action=\"\" value=\"Name\" width=150 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Name
        sb.append("<td> <button action=\"\" value=\"Level\" width=75 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Level
        sb.append("<td> <button action=\"\" value=\"Old Price\" width=100 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Old price
        sb.append("<td> <button action=\"\" value=\"New Price\" width=125 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // New price
        sb.append("<td> <button action=\"\" value=\"Action\" width=125 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Change Price
        sb.append("<td> <button action=\"\" value=\"Remove\" width=85 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Remove Buff
        sb.append("<td fixwidth=\"20\"></td>");
        sb.append("</tr>");

        if (player.getSellingBuffs().isEmpty()) {
            sb.append("</table>");
            sb.append("<br><br><br>");
            sb.append("You don't have added any buffs yet!");
        } else {
            for (SellBuffHolder holder : player.getSellingBuffs()) {
                final Skill skill = player.getKnownSkill(holder.getSkillId());
                if (skill == null) {
                    continue;
                }

                sb.append("<tr>");
                sb.append("<td fixwidth=\"20\"></td>");
                sb.append("<td align=center><img src=\"" + skill.getIcon() + "\" width=\"32\" height=\"32\"></td>"); // Icon
                sb.append("<td align=left>" + skill.getName() + (skill.getLevel() > 100 ? "<font color=\"LEVEL\"> + " + (skill.getLevel() % 100) + "</font></td>" : "</td>")); // Name + enchant
                sb.append("<td align=center>" + ((skill.getLevel() > 100) ? SkillEngine.getInstance().getMaxLevel(skill.getId()) : skill.getLevel()) + "</td>"); // Level
                sb.append("<td align=center> " + GameUtils.formatAdena(holder.getPrice()) + " </td>"); // Price show
                sb.append("<td align=center><edit var=\"price_" + skill.getId() + "\" width=120 type=\"number\"></td>"); // Price edit
                sb.append("<td align=center><button value=\"Edit\" action=\"bypass -h sellbuffchangeprice " + skill.getId() + " $price_" + skill.getId() + "\" width=\"85\" height=\"26\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                sb.append("<td align=center><button value=\" X \" action=\"bypass -h sellbuffremove " + skill.getId() + "\" width=\"26\" height=\"26\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                sb.append("</tr>");
                sb.append("<tr><td><br><br></td></tr>");
            }
            sb.append("</table>");
        }

        return sb.toString();
    }

    private String buildSkillMenu(Player player, int index) {
        final int ceiling = index + 10;
        int nextIndex = -1;
        int previousIndex = -1;
        final StringBuilder sb = new StringBuilder();
        final List<Skill> skillList = new ArrayList<>();

        int count = 0;
        for (Skill skill : player.getAllSkills()) {
            if (ALLOWED_BUFFS.contains(skill.getId()) && !isInSellList(player, skill)) {
                count++;

                if ((count > index) && (count <= ceiling)) {
                    skillList.add(skill);
                }
            }
        }

        if (count > 10) {
            if (count > (index + 10)) {
                nextIndex = index + 10;
            }
        }

        if (index >= 10) {
            previousIndex = index - 10;
        }

        sb.append("<table border=0 cellpadding=0 cellspacing=0 background=\"L2UI_CH3.refinewnd_back_Pattern\">");
        sb.append("<tr><td><br><br><br></td></tr>");
        sb.append("<tr>");
        sb.append("<td fixwidth=\"10\"></td>");
        sb.append("<td> <button action=\"\" value=\"Icon\" width=100 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Icon
        sb.append("<td> <button action=\"\" value=\"Name\" width=175 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Name
        sb.append("<td> <button action=\"\" value=\"Level\" width=150 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Leve
        sb.append("<td> <button action=\"\" value=\"Price\" width=150 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Price
        sb.append("<td> <button action=\"\" value=\"Action\" width=125 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>"); // Action
        sb.append("<td fixwidth=\"20\"></td>");
        sb.append("</tr>");

        if (skillList.isEmpty()) {
            sb.append("</table>");
            sb.append("<br><br><br>");
            sb.append("At this moment you cant add any buffs!");
        } else {
            for (Skill skill : skillList) {
                sb.append("<tr>");
                sb.append("<td fixwidth=\"20\"></td>");
                sb.append("<td align=center><img src=\"" + skill.getIcon() + "\" width=\"32\" height=\"32\"></td>");
                sb.append("<td align=left>" + skill.getName() + (skill.getLevel() > 100 ? "<font color=\"LEVEL\"> + " + (skill.getLevel() % 100) + "</font></td>" : "</td>"));
                sb.append("<td align=center>" + ((skill.getLevel() > 100) ? SkillEngine.getInstance().getMaxLevel(skill.getId()) : skill.getLevel()) + "</td>");
                sb.append("<td align=center><edit var=\"price_" + skill.getId() + "\" width=120 type=\"number\"></td>");
                sb.append("<td align=center fixwidth=\"50\"><button value=\"Add Buff\" action=\"bypass -h sellbuffaddskill " + skill.getId() + " $price_" + skill.getId() + "\" width=\"85\" height=\"26\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                sb.append("</tr>");
                sb.append("<tr><td><br><br></td></tr>");
            }
            sb.append("</table>");
        }

        sb.append("<table width=\"250\" border=\"0\">");
        sb.append("<tr>");

        if (previousIndex > -1) {
            sb.append("<td align=left><button value=\"Previous Page\" action=\"bypass -h sellbuffadd " + previousIndex + "\" width=\"100\" height=\"30\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        }

        if (nextIndex > -1) {
            sb.append("<td align=right><button value=\"Next Page\" action=\"bypass -h sellbuffadd " + nextIndex + "\" width=\"100\" height=\"30\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        }
        sb.append("</tr>");
        sb.append("</table>");
        return sb.toString();
    }

    public boolean isInSellList(Player player, Skill skill) {
        return player.getSellingBuffs().stream().filter(h -> (h.getSkillId() == skill.getId())).findFirst().orElse(null) != null;
    }

    public boolean canStartSellBuffs(Player player) {
        if (player.isAlikeDead()) {
            player.sendMessage("You can't sell buffs in fake death!");
            return false;
        } else if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player)) {
            player.sendMessage("You can't sell buffs with Olympiad status!");
            return false;
        } else if (player.isOnEvent()) // custom event message
        {
            player.sendMessage("You can't sell buffs while registered in an event!");
            return false;
        } else if (player.getReputation() < 0) {
            player.sendMessage("You can't sell buffs in Chaotic state!");
            return false;
        } else if (player.isInDuel()) {
            player.sendMessage("You can't sell buffs in Duel state!");
            return false;
        } else if (player.isFishing()) {
            player.sendMessage("You can't sell buffs while fishing.");
            return false;
        } else if (player.isMounted() || player.isFlyingMounted() || player.isFlying()) {
            player.sendMessage("You can't sell buffs in Mount state!");
            return false;
        } else if (player.isTransformed()) {
            player.sendMessage("You can't sell buffs in Transform state!");
            return false;
        } else if (player.isInsideZone(ZoneType.NO_STORE) || !player.isInsideZone(ZoneType.PEACE) || player.isJailed()) {
            player.sendMessage("You can't sell buffs here!");
            return false;
        }
        return true;
    }

    public static SellBuffsManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final SellBuffsManager INSTANCE = new SellBuffsManager();
    }
}