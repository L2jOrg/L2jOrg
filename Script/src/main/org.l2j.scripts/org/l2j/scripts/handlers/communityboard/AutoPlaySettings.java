/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.handlers.communityboard;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IParseBoardHandler;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.serverpackets.ExServerPrimitive;
import org.l2j.scripts.ai.others.DimensionalMerchant.DimensionalMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.nio.file.Path;
import java.util.*;

/**
 * @author: Bru7aLMike
 **/

public class AutoPlaySettings extends AbstractScript implements IParseBoardHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoPlaySettings.class);
    private static final String[] CMD = new String[]{"_bbsautoplay"};

    @RegisterEvent(EventType.ON_PLAYER_LOGIN)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLogin(OnPlayerLogin event)
    {
        final Player activeChar = event.getPlayer();

        var closeRange = activeChar.getAutoPlayRangeClose();
        var longRange = activeChar.getAutoPlayRangeLong();
        var viewRange = activeChar.showAutoPlayRadius();
        var centered = activeChar.isAutoPlayZoneAnchored();

        if(closeRange > 0 && closeRange <= 800 && closeRange != Config.AUTO_PLAY_CLOSE_RANGE) {
            LOGGER.info("AUTOPLAY: Restoring close range settings for [{}] to [" + closeRange + "].", activeChar);
        }
        else {
            LOGGER.info("AUTOPLAY: Default close range settings for [{}] applied.", activeChar);
            setDefaultByCommand(activeChar, "closeRange");
        }

        if(longRange >= 800 && longRange <= 2000 && longRange != Config.AUTO_PLAY_LONG_RANGE) {
            LOGGER.info("AUTOPLAY: Restoring long range settings for [{}] to [" + longRange + "].", activeChar);
        }
        else {
            LOGGER.info("AUTOPLAY: Default long range settings for [{}] applied.", activeChar);
            setDefaultByCommand(activeChar, "longRange");
        }

        if(viewRange) {
            setDefaultByCommand(activeChar, "viewRange");
        }

        if(centered) {
            setDefaultByCommand(activeChar, "centered");
        }
    }

    /*
     * Could be handy if the ExServerPrimitive are not
     * being deleted on player logout
     */
    /*
    @RegisterEvent(EventType.ON_PLAYER_LOGOUT)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLogOut(OnPlayerLogout event)
    {
        final Player activeChar = event.getPlayer();

        var closeRange = activeChar.getAutoPlayRangeClose();
        var longRange = activeChar.getAutoPlayRangeLong();
        var viewRange = activeChar.showAutoPlayRadius();
        var centered = activeChar.isAutoPlayZoneAnchored();

        if(closeRange > 0 && closeRange <= 800 && closeRange != Config.AUTO_PLAY_CLOSE_RANGE) {
            LOGGER.info("AUTOPLAY: Saving close range settings for [{}].", activeChar);
            executeCommand(activeChar, "closeRange", closeRange);
        }

        if(longRange >= 800 && longRange <= 2000 && longRange != Config.AUTO_PLAY_LONG_RANGE) {
            LOGGER.info("AUTOPLAY: Saving long range settings for [{}].", activeChar);
            executeCommand(activeChar, "longRange", longRange);
        }

        if(viewRange) {

            if (centered){
                setDefaultByCommand(activeChar, "centered");
                setDefaultByCommand(activeChar, "viewRange");
            } else {
            setDefaultByCommand(activeChar, "viewRange"); }
        }
        else{
            if (centered) {
            setDefaultByCommand(activeChar, "centered");
            }
        }
    }
    */

    @Override
    public boolean parseCommunityBoardCommand(String command, StringTokenizer tokens, Player player) {
        String subCommand = tokens.nextToken();
        String html = null;

        if(subCommand.equalsIgnoreCase("settings")) {
            // html = showAutoplaySettings(player);
            DimensionalMerchant.openHtml(player, "e_premium_manager_custom_autoplaysettings.html");
            player.clearHtmlActions(HtmlActionScope.COMM_BOARD_HTML);
        }

        else if(subCommand.equalsIgnoreCase("closeRange")) {
            if (!tokens.hasMoreElements()){
                displayMessageOnScreen(player, "You need to specify a value between 150 and 800!");
                return false;
            }
            processCommand(player, subCommand, tokens);
            // html = showAutoplaySettings(player);
            DimensionalMerchant.openHtml(player, "e_premium_manager_custom_autoplaysettings.html");
        }
        else if(subCommand.equalsIgnoreCase("longRange")) {
            if (!tokens.hasMoreElements()){
                displayMessageOnScreen(player, "You need to specify a value between 800 and 2000!");
                return false;
            }
            processCommand(player, subCommand, tokens);
            // html = showAutoplaySettings(player);
            DimensionalMerchant.openHtml(player, "e_premium_manager_custom_autoplaysettings.html");
        }
        else if(subCommand.equalsIgnoreCase("viewRange")) {
            if (!tokens.hasMoreElements()){
                return false;
            }
            processCommand(player, subCommand, tokens);
            // html = showAutoplaySettings(player);
            DimensionalMerchant.openHtml(player, "e_premium_manager_custom_autoplaysettings.html");
        }
        else if(subCommand.equalsIgnoreCase("centered")) {
            if (!tokens.hasMoreElements()){
                return false;
            }
            processCommand(player, subCommand, tokens);
            // html = showAutoplaySettings(player);
            DimensionalMerchant.openHtml(player, "e_premium_manager_custom_autoplaysettings.html");
        }

        // for use in ALT+B along with html = showAutoplaySettings(player);
        // CommunityBoardHandler.separateAndSend(html, player);
        return true;
    }

    private void processCommand(Player activeChar, String command, StringTokenizer params) {

        ExServerPrimitive debug_low = activeChar.getDebugPacket("radiusLow");
        ExServerPrimitive debug_mid = activeChar.getDebugPacket("radiusMid");
        ExServerPrimitive debug_high = activeChar.getDebugPacket("radiusHigh");
        ExServerPrimitive debug_center = activeChar.getDebugPacket("center");

        // var radiusLow = ExServerPrimitive.createCirclePacket("radiusLow", activeChar.getAutoPlayAnchorX(), activeChar.getAutoPlayAnchorY(), activeChar.getAutoPlayAnchorZ() + 25, ((activeChar.getAutoPlaySettings().isNearTarget()) ? activeChar.getAutoPlayRangeClose() : activeChar.getAutoPlayRangeLong()), Color.BLUE, activeChar.getAutoPlayAnchorX(), activeChar.getAutoPlayAnchorY());
        var radiusMid = ExServerPrimitive.createCirclePacket("radiusMid", activeChar.getAutoPlayAnchorX(), activeChar.getAutoPlayAnchorY(), activeChar.getAutoPlayAnchorZ() + 75, ((activeChar.getAutoPlaySettings().isNearTarget()) ? activeChar.getAutoPlayRangeClose() : activeChar.getAutoPlayRangeLong()) - 25, Color.GREEN, activeChar.getAutoPlayAnchorX(), activeChar.getAutoPlayAnchorY());
        /* var radiusHigh = ExServerPrimitive.createCirclePacket("radiusHigh", activeChar.getAutoPlayAnchorX(), activeChar.getAutoPlayAnchorY(), activeChar.getAutoPlayAnchorZ() + 150, ((activeChar.getAutoPlaySettings().isNearTarget()) ? activeChar.getAutoPlayRangeClose() : activeChar.getAutoPlayRangeLong()) - 75, Color.RED, activeChar.getAutoPlayAnchorX(), activeChar.getAutoPlayAnchorY());
        var dynamicRadiusLow = ExServerPrimitive.createCirclePacket("radiusLow", activeChar.getX(), activeChar.getY(), activeChar.getZ() + 25, ((activeChar.getAutoPlaySettings().isNearTarget()) ? activeChar.getAutoPlayRangeClose() : activeChar.getAutoPlayRangeLong()), Color.GREEN, activeChar.getX(), activeChar.getY());
        var dynamicRadiusMid = ExServerPrimitive.createCirclePacket("radiusMid", activeChar.getX(), activeChar.getY(), activeChar.getZ() + 75, ((activeChar.getAutoPlaySettings().isNearTarget()) ? activeChar.getAutoPlayRangeClose() : activeChar.getAutoPlayRangeLong()) - 25, Color.ORANGE, activeChar.getX(), activeChar.getY());
        var dynamicRadiusHigh = ExServerPrimitive.createCirclePacket("radiusHigh", activeChar.getX(), activeChar.getY(), activeChar.getZ() + 150, ((activeChar.getAutoPlaySettings().isNearTarget()) ? activeChar.getAutoPlayRangeClose() : activeChar.getAutoPlayRangeLong()) - 75, Color.RED, activeChar.getX(), activeChar.getY());
         */
        var center = ExServerPrimitive.createCirclePacket("center", activeChar.getAutoPlayAnchorX(), activeChar.getAutoPlayAnchorY(), activeChar.getAutoPlayAnchorZ() + 25, (int) activeChar.getCollisionRadius(), Color.PINK, activeChar.getAutoPlayAnchorX(), activeChar.getAutoPlayAnchorY());
        //var dynamicCenter = ExServerPrimitive.createCirclePacket("center", activeChar.getX(), activeChar.getY(), activeChar.getZ() + 25, (int) activeChar.getCollisionRadius(), Color.PINK, activeChar.getX(), activeChar.getY());

        if (params == null)
        {
            displayMessageOnScreen(activeChar, "INCORRECT VALUE!");
            return;
        }
        switch (command) {
            case "closeRange":
                var range = Math.min(800, Integer.parseInt(params.nextToken()));
                try {
                    if (range <= 0) {
                        activeChar.sendMessage("You cannot specify 0 (zero) or a negative value!");
                        return;
                    }

                    if (range > 800) {
                        activeChar.sendMessage("You cannot specify a value greater than 800!");
                        return;
                    }

                    executeCommand(activeChar, "closeRange", range);

                } catch (NumberFormatException e) {
                    activeChar.sendMessage("Incorrect number!");
                }
                break;
            case "longRange":
                try {
                    var range2 = Math.min(2000, Integer.parseInt(params.nextToken()));
                    if (range2 < 800) {
                        activeChar.sendMessage("You cannot specify a value lower than 800.");
                        return;
                    }
                    if (range2 > 2000) {
                        activeChar.sendMessage("You cannot specify a value greater than 2000!");
                        return;
                    }

                    executeCommand(activeChar, "longRange", range2);

                } catch (NumberFormatException e) {
                    activeChar.sendMessage("Incorrect number!");
                }
                break;
            case "viewRange":
                var param = Integer.parseInt(params.nextToken());
                if (param != 0) {
                    if (activeChar.getAutoPlaySettings().isActive())
                    {
                        if (!activeChar.isAutoPlayZoneAnchored())
                        {
                            displayMessageOnScreen(activeChar, "The farmzone must be centered to do that.");
                            break;
                        }

                        if (!activeChar.showAutoPlayRadius())
                        {
                                setBooleanSettingsByCommand(activeChar, command);
                                activeChar.sendPacket(center);
                                // activeChar.sendPacket(radiusLow);
                                activeChar.sendPacket(radiusMid);
                                // activeChar.sendPacket(radiusHigh);
                        }
                        else
                        {
                            setDefaultByCommand(activeChar, command);
                            activeChar.clearDebugPackets();
                            activeChar.sendPacket(center);
                        }
                    }
                    else
                    {
                        if (!activeChar.isAutoPlayZoneAnchored() && !activeChar.showAutoPlayRadius())
                        {
                            displayMessageOnScreen(activeChar, "Farmzone must be centered to do that.");
                            break;
                        }

                        if (!activeChar.showAutoPlayRadius())
                        {
                            setBooleanSettingsByCommand(activeChar, command);
                            activeChar.sendPacket(center);
                            // activeChar.sendPacket(radiusLow);
                            activeChar.sendPacket(radiusMid);
                            // activeChar.sendPacket(radiusHigh);
                        }
                        else {
                            setDefaultByCommand(activeChar, command);
                            debug_low.reset();
                            debug_mid.reset();
                            debug_high.reset();
                            activeChar.clearDebugPackets();
                        }
                    }
                }
                break;
            case "centered":
                var param2 = Integer.parseInt(params.nextToken());
                if (param2 == 1) {

                    // farm zone is STATIC/ isCentered
                    // so it becomes Dynamic
                    if (activeChar.isAutoPlayZoneAnchored())
                    {
                        // Autoplay ON
                        if (activeChar.getAutoPlaySettings().isActive())
                        {
                            if (activeChar.showAutoPlayRadius())
                            {
                                activeChar.setAutoPlayRadius(false);
                                debug_low.reset();
                                debug_mid.reset();
                                debug_high.reset();
                            }

                            AutoPlayEngine.getInstance().stopAutoPlay(activeChar);
                            setDefaultByCommand(activeChar, command);
                            AutoPlayEngine.getInstance().startAutoPlay(activeChar);
                            debug_center.reset();
                            activeChar.clearDebugPackets();
                        }

                        // Autoplay OFF
                        else
                        {
                            if (activeChar.showAutoPlayRadius())
                            {
                                activeChar.setAutoPlayRadius(false);
                                debug_low.reset();
                                debug_mid.reset();
                                debug_high.reset();
                            }

                            setDefaultByCommand(activeChar, command);
                            debug_center.reset();
                            activeChar.clearDebugPackets();
                        }
                    }

                    // farm zone is DYNAMIC / becomes centered
                    else
                    {
                        // Autoplay ON
                        if (activeChar.getAutoPlaySettings().isActive())
                        {
                            if (activeChar.showAutoPlayRadius())
                            {
                                debug_low.reset();
                                debug_mid.reset();
                                debug_high.reset();
                                activeChar.clearDebugPackets();

                                // activeChar.sendPacket(radiusLow);
                                activeChar.sendPacket(radiusMid);
                                // activeChar.sendPacket(radiusHigh);
                            }
                            AutoPlayEngine.getInstance().stopAutoPlay(activeChar);
                            setBooleanSettingsByCommand(activeChar, command);
                            AutoPlayEngine.getInstance().startAutoPlay(activeChar);
                            activeChar.sendPacket(center);
                        }

                        // Autoplay OFF
                        else
                        {
                            if (activeChar.showAutoPlayRadius())
                            {
                                debug_low.reset();
                                debug_mid.reset();
                                debug_high.reset();
                                activeChar.clearDebugPackets();

                                // activeChar.sendPacket(radiusLow);
                                activeChar.sendPacket(radiusMid);
                                // activeChar.sendPacket(radiusHigh);
                            }
                            setBooleanSettingsByCommand(activeChar, command);
                        }
                    }
                }
                break;
            default :
                LOGGER.info("[" + activeChar.getName() + "] has entered an unknown command!");
                break;
        }
    }

    private void executeCommand(Player activeChar, String command, int range) {
        String message;
        switch (command) {
            case "closeRange" -> {
                if (range >= 150 && range <= 800) {
                    setPersonalRangeByCommand(activeChar, "closeRange", range);

                    message = "You have just changed your close range settings. Your new close range is [" + range + "].";
                } else {
                    setDefaultByCommand(activeChar, "closeRange");

                    message = "The input value is out of bounds. Applying defaults for close range - [" + Config.AUTO_PLAY_CLOSE_RANGE + "].)";
                }
                displayMessageOnScreen(activeChar, message);
            }
            case "longRange" -> {
                if (range >= 800 && range <= 2000) {
                    setPersonalRangeByCommand(activeChar, "longRange", range);

                    message = "You have just changed your long range settings. Your new long range is [" + range + "].";
                } else {
                    setDefaultByCommand(activeChar, "longRange");

                    message = "The input value is out of bounds. Applying defaults for long range - [" + Config.AUTO_PLAY_LONG_RANGE + "].";
                }
                displayMessageOnScreen(activeChar, message);
            }
        }
    }

    private static void setDefaultByCommand(Player activeChar, String command) {
        switch (command) {
            case "closeRange" -> activeChar.setAutoPlayRangeClose(Config.AUTO_PLAY_CLOSE_RANGE);
            case "longRange" -> activeChar.setAutoPlayRangeLong(Config.AUTO_PLAY_LONG_RANGE);
            case "viewRange" -> activeChar.setAutoPlayRadius(false);
            case "centered" -> {
                activeChar.setAnchorAutoPlayZone(false);
                activeChar.setAutoPlayAnchorX(0);
                activeChar.setAutoPlayAnchorY(0);
                activeChar.setAutoPlayAnchorZ(0);
            }
        }
    }

    private static void setPersonalRangeByCommand(Player activeChar, String command, int range)  {
        switch (command) {
            case "closeRange" -> activeChar.setAutoPlayRangeClose(range);
            case "longRange" -> activeChar.setAutoPlayRangeLong(range);
            default -> activeChar.sendMessage("ERROR CODE: J-435");
        }
    }

    private void setBooleanSettingsByCommand(Player activeChar, String command) {
        switch (command) {
            case "viewRange" -> activeChar.setAutoPlayRadius(true);
            case "centered" -> {
                activeChar.setAutoPlayAnchorX(activeChar.getX());
                activeChar.setAutoPlayAnchorY(activeChar.getY());
                activeChar.setAutoPlayAnchorZ(activeChar.getZ());
                activeChar.setAnchorAutoPlayZone(true);
            }
            default -> activeChar.sendMessage("ERROR CODE: J-448");
        }
    }

    private static void displayMessageOnScreen(Player player, String message) {
        showOnScreenMsg(player, message, 5000);
    }

    /**
     * AutoPlay settings bloc for community board.
     * DO NOT DELETE! I plan to make configs.
     */

    /*
    public String showAutoplaySettings(Player player)
    {
        String NAVIGATION_PATH = "data/html/CommunityBoard/Custom/new/navigation.html";
        String html = ("data/html/CommunityBoard/Custom/new/autoplaysettings.html");

        // custom Autoplay variables
        var closeRange = player.getAutoPlayRangeClose();
        var longRange = player.getAutoPlayRangeLong();
        boolean viewRange = player.showAutoPlayRadius();
        boolean centrePoint = player.isAutoPlayZoneAnchored();

        if (closeRange == 0)
        {
            closeRange = Config.AUTO_PLAY_CLOSE_RANGE;
        }
        if (longRange == 0)
        {
            longRange = Config.AUTO_PLAY_LONG_RANGE;
        }
        // custom AutoPlay settings
        final String navigation = HtmCache.getInstance().getHtm(player, NAVIGATION_PATH);
        html = html.replace("%navigation%", navigation);
        html = html.replace("%closeRange%", (closeRange <= 0 || closeRange > 800) ? String.valueOf(Config.AUTO_PLAY_CLOSE_RANGE) : String.valueOf(closeRange));
        html = html.replace("%longRange%", (longRange < 800 || longRange > 2000) ? String.valueOf(Config.AUTO_PLAY_LONG_RANGE) : String.valueOf(longRange));
        html = html.replace("%viewRange%", (viewRange ? "<font color=24a10e> ON </font>" : "<font color=ff0a0a> OFF </font>"));
        html = html.replace("%centered%", (centrePoint ? "<font color=ff0a0a> CENTERED </font>" : "<font color=24a10e> DYNAMIC </font>"));
        html = html.replace("?closeRange?", ("<button value=Set action="+"bypass _bbsautoplay closeRange $closeRange"+" width=85 height=26 back=L2UI_CT1.LCoinShopWnd.LCoinShopWnd_DF_Button fore=L2UI_CT1.LCoinShopWnd.LCoinShopWnd_DF_Button>"));
        html = html.replace("?longRange?", ("<button value=Set action="+"bypass _bbsautoplay longRange $longRange"+" width=85 height=26 back=L2UI_CT1.LCoinShopWnd.LCoinShopWnd_DF_Button fore=L2UI_CT1.LCoinShopWnd.LCoinShopWnd_DF_Button>"));
        html = html.replace("?viewRange?", (viewRange ? "HIDE" : "SHOW"));
        html = html.replace("?centered?", (centrePoint ? "Change to Dynamic" : "Change to Static"));
        return html;

    }
     */

    @Override
    public String[] getCommunityBoardCommands() {
        return CMD;
    }

    @Override
    public final String getScriptName() {
        return getClass().getSimpleName();
    }

    @Override
    public final Path getScriptPath() {
        return null;
    }

}