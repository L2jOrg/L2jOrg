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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcManorBypass;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMenuSelect;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

import static org.l2j.commons.util.Util.isInteger;
import static org.l2j.gameserver.util.GameUtils.*;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;

/**
 * RequestBypassToServer client packet implementation.
 *
 * @author HorridoJoho
 */
public final class RequestBypassToServer extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBypassToServer.class);

    // FIXME: This is for compatibility, will be changed when bypass functionality got an overhaul by NosBit
    private static final String[] nonHtmBypasses = {
        "admin",
        "_bbs",
        "bbs",
        "_mail",
        "_friend",
        "_match",
        "_diary",
        "_olympiad?command",
        "menu_select",
        "manor_menu_select",
        "pccafe"
    };

    private String bypass;

    private static void comeHere(Player player) {
        final WorldObject obj = player.getTarget();
        if (isNpc(obj)) {
            final Npc temp = (Npc) obj;
            temp.setTarget(player);
            temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, player.getLocation());
        }
    }

    @Override
    public void readImpl() {
        bypass = readString();
    }

    @Override
    public void runImpl() {
        var player = client.getPlayer();

        if (bypass.isEmpty()) {
            LOGGER.warn("Player {} sent empty bypass!", player);
            Disconnection.of(client, player).defaultSequence(false);
            return;
        }

        boolean requiresBypassValidation = true;
        for (String possibleNonHtmlCommand : nonHtmBypasses) {
            if (bypass.startsWith(possibleNonHtmlCommand)) {
                requiresBypassValidation = false;
                break;
            }
        }

        int bypassOriginId = 0;
        if (requiresBypassValidation && !player.isGM()) {
            bypassOriginId = player.validateHtmlAction(bypass);
            if (bypassOriginId == -1) {
                LOGGER.warn("Player {} sent non cached bypass: '{}'", player.getName(), bypass);
                return;
            }

            if ((bypassOriginId > 0) && !GameUtils.isInsideRangeOfObjectId(player, bypassOriginId, Npc.INTERACTION_DISTANCE)) {
                // No logging here, this could be a common case where the player has the html still open and run too far away and then clicks a html action
                return;
            }
        }

        if (!client.getFloodProtectors().getServerBypass().tryPerformAction(bypass)) {
            return;
        }

        final TerminateReturn terminateReturn = EventDispatcher.getInstance().notifyEvent(new OnPlayerBypass(player, bypass), player, TerminateReturn.class);
        if ((terminateReturn != null) && terminateReturn.terminate()) {
            return;
        }

        try {
            if (bypass.startsWith("admin_")) {
                AdminCommandHandler.getInstance().useAdminCommand(player, bypass, true);
            } else if (CommunityBoardHandler.getInstance().isCommunityBoardCommand(bypass)) {
                CommunityBoardHandler.getInstance().handleParseCommand(bypass, player);
            } else if (bypass.equals("come_here") && player.isGM()) {
                comeHere(player);
            } else if (bypass.startsWith("npc_")) {
                final int endOfId = bypass.indexOf('_', 5);
                String id;
                if (endOfId > 0) {
                    id = bypass.substring(4, endOfId);
                } else {
                    id = bypass.substring(4);
                }

                if (isInteger(id)) {
                    final WorldObject object = World.getInstance().findObject(Integer.parseInt(id));

                    if (isNpc(object) && (endOfId > 0) && isInsideRadius2D(player, object, Npc.INTERACTION_DISTANCE)) {
                        ((Npc) object).onBypassFeedback(player, bypass.substring(endOfId + 1));
                    }
                }

                player.sendPacket(ActionFailed.STATIC_PACKET);
            } else if (bypass.startsWith("item_")) {
                final int endOfId = bypass.indexOf('_', 5);
                String id;
                if (endOfId > 0) {
                    id = bypass.substring(5, endOfId);
                } else {
                    id = bypass.substring(5);
                }
                try {
                    final Item item = player.getInventory().getItemByObjectId(Integer.parseInt(id));
                    if ((item != null) && (endOfId > 0)) {
                        item.onBypassFeedback(player, bypass.substring(endOfId + 1));
                    }

                    player.sendPacket(ActionFailed.STATIC_PACKET);
                } catch (NumberFormatException nfe) {
                    LOGGER.warn("NFE for command [" + bypass + "]", nfe);
                }
            } else if (bypass.startsWith("_match")) {
                final String params = bypass.substring(bypass.indexOf("?") + 1);
                final StringTokenizer st = new StringTokenizer(params, "&");
                final int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heroid = Hero.getInstance().getHeroByClass(heroclass);
                if (heroid > 0) {
                    Hero.getInstance().showHeroFights(player, heroclass, heroid, heropage);
                }
            } else if (bypass.startsWith("_diary")) {
                final String params = bypass.substring(bypass.indexOf("?") + 1);
                final StringTokenizer st = new StringTokenizer(params, "&");
                final int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heroid = Hero.getInstance().getHeroByClass(heroclass);
                if (heroid > 0) {
                    Hero.getInstance().showHeroDiary(player, heroclass, heroid, heropage);
                }
            } else if (bypass.startsWith("_olympiad?command")) {
                final int arenaId = Integer.parseInt(bypass.split("=")[2]);
                final IBypassHandler handler = BypassHandler.getInstance().getHandler("arenachange");
                if (handler != null) {
                    handler.useBypass("arenachange " + (arenaId - 1), player, null);
                }
            } else if (bypass.startsWith("menu_select")) {
                final Npc lastNpc = player.getLastFolkNPC();
                if ((lastNpc != null) && lastNpc.canInteract(player)) {
                    final String[] split = bypass.substring(bypass.indexOf("?") + 1).split("&");
                    final int ask = Integer.parseInt(split[0].split("=")[1]);
                    final int reply = Integer.parseInt(split[1].split("=")[1]);
                    EventDispatcher.getInstance().notifyEventAsync(new OnNpcMenuSelect(player, lastNpc, ask, reply), lastNpc);
                }
            } else if (bypass.startsWith("manor_menu_select")) {
                final Npc lastNpc = player.getLastFolkNPC();
                if (Config.ALLOW_MANOR && (lastNpc != null) && lastNpc.canInteract(player)) {
                    final String[] split = bypass.substring(bypass.indexOf("?") + 1).split("&");
                    final int ask = Integer.parseInt(split[0].split("=")[1]);
                    final int state = Integer.parseInt(split[1].split("=")[1]);
                    final boolean time = split[2].split("=")[1].equals("1");
                    EventDispatcher.getInstance().notifyEventAsync(new OnNpcManorBypass(player, lastNpc, ask, state, time), lastNpc);
                }
            } else if (bypass.startsWith("pccafe")) {
                if (!Config.PC_CAFE_ENABLED) {
                    return;
                }
                final int multisellId = Integer.parseInt(bypass.substring(10).trim());
                MultisellData.getInstance().separateAndSend(multisellId, player, null, false);
            } else {
                final IBypassHandler handler = BypassHandler.getInstance().getHandler(bypass);
                if (handler != null) {
                    if (bypassOriginId > 0) {
                        final WorldObject bypassOrigin = World.getInstance().findObject(bypassOriginId);
                        if (isCreature(bypassOrigin)) {
                            handler.useBypass(bypass, player, (Creature) bypassOrigin);
                        } else {
                            handler.useBypass(bypass, player, null);
                        }
                    } else {
                        handler.useBypass(bypass, player, null);
                    }
                } else {
                    LOGGER.warn(client + " sent not handled RequestBypassToServer: [" + bypass + "]");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Exception processing bypass from player " + player.getName() + ": " + bypass, e);

            if (player.isGM()) {
                final StringBuilder sb = new StringBuilder(200);
                sb.append("<html><body>").append("Bypass error: ").append(e).append("<br1>")
                .append("Bypass command: ").append(bypass).append("<br1>")
                .append("StackTrace:<br1>");

                for (StackTraceElement ste : e.getStackTrace()) {
                    sb.append(ste).append("<br1>");
                }
                sb.append("</body></html>");
                // item html
                final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1, sb.toString());
                msg.disableValidation();
                player.sendPacket(msg);
            }
        }
    }
}
