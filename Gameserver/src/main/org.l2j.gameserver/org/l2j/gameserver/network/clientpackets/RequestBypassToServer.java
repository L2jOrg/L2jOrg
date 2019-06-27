package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcManorBypass;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMenuSelect;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

/**
 * RequestBypassToServer client packet implementation.
 *
 * @author HorridoJoho
 */
public final class RequestBypassToServer extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBypassToServer.class);

    // FIXME: This is for compatibility, will be changed when bypass functionality got an overhaul by NosBit
    private static final String[] _possibleNonHtmlCommands =
            {
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

    // S
    private String _command;

    /**
     * @param activeChar
     */
    private static void comeHere(L2PcInstance activeChar) {
        final L2Object obj = activeChar.getTarget();
        if (obj == null) {
            return;
        }
        if (obj.isNpc()) {
            final L2Npc temp = (L2Npc) obj;
            temp.setTarget(activeChar);
            temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, activeChar.getLocation());
        }
    }

    @Override
    public void readImpl() {
        _command = readString();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (_command.isEmpty()) {
            LOGGER.warn("Player " + activeChar.getName() + " sent empty bypass!");
            Disconnection.of(client, activeChar).defaultSequence(false);
            return;
        }

        boolean requiresBypassValidation = true;
        for (String possibleNonHtmlCommand : _possibleNonHtmlCommands) {
            if (_command.startsWith(possibleNonHtmlCommand)) {
                requiresBypassValidation = false;
                break;
            }
        }

        int bypassOriginId = 0;
        if (requiresBypassValidation) {
            bypassOriginId = activeChar.validateHtmlAction(_command);
            if (bypassOriginId == -1) {
                LOGGER.warn("Player " + activeChar.getName() + " sent non cached bypass: '" + _command + "'");
                return;
            }

            if ((bypassOriginId > 0) && !GameUtils.isInsideRangeOfObjectId(activeChar, bypassOriginId, L2Npc.INTERACTION_DISTANCE)) {
                // No logging here, this could be a common case where the player has the html still open and run too far away and then clicks a html action
                return;
            }
        }

        if (!client.getFloodProtectors().getServerBypass().tryPerformAction(_command)) {
            return;
        }

        final TerminateReturn terminateReturn = EventDispatcher.getInstance().notifyEvent(new OnPlayerBypass(activeChar, _command), activeChar, TerminateReturn.class);
        if ((terminateReturn != null) && terminateReturn.terminate()) {
            return;
        }

        try {
            if (_command.startsWith("admin_")) {
                AdminCommandHandler.getInstance().useAdminCommand(activeChar, _command, true);
            } else if (CommunityBoardHandler.getInstance().isCommunityBoardCommand(_command)) {
                CommunityBoardHandler.getInstance().handleParseCommand(_command, activeChar);
            } else if (_command.equals("come_here") && activeChar.isGM()) {
                comeHere(activeChar);
            } else if (_command.startsWith("npc_")) {
                final int endOfId = _command.indexOf('_', 5);
                String id;
                if (endOfId > 0) {
                    id = _command.substring(4, endOfId);
                } else {
                    id = _command.substring(4);
                }

                if (Util.isNumeric(id)) {
                    final L2Object object = L2World.getInstance().findObject(Integer.parseInt(id));

                    if ((object != null) && object.isNpc() && (endOfId > 0) && activeChar.isInsideRadius2D(object, L2Npc.INTERACTION_DISTANCE)) {
                        ((L2Npc) object).onBypassFeedback(activeChar, _command.substring(endOfId + 1));
                    }
                }

                activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            } else if (_command.startsWith("item_")) {
                final int endOfId = _command.indexOf('_', 5);
                String id;
                if (endOfId > 0) {
                    id = _command.substring(5, endOfId);
                } else {
                    id = _command.substring(5);
                }
                try {
                    final L2ItemInstance item = activeChar.getInventory().getItemByObjectId(Integer.parseInt(id));
                    if ((item != null) && (endOfId > 0)) {
                        item.onBypassFeedback(activeChar, _command.substring(endOfId + 1));
                    }

                    activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                } catch (NumberFormatException nfe) {
                    LOGGER.warn("NFE for command [" + _command + "]", nfe);
                }
            } else if (_command.startsWith("_match")) {
                final String params = _command.substring(_command.indexOf("?") + 1);
                final StringTokenizer st = new StringTokenizer(params, "&");
                final int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heroid = Hero.getInstance().getHeroByClass(heroclass);
                if (heroid > 0) {
                    Hero.getInstance().showHeroFights(activeChar, heroclass, heroid, heropage);
                }
            } else if (_command.startsWith("_diary")) {
                final String params = _command.substring(_command.indexOf("?") + 1);
                final StringTokenizer st = new StringTokenizer(params, "&");
                final int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heroid = Hero.getInstance().getHeroByClass(heroclass);
                if (heroid > 0) {
                    Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
                }
            } else if (_command.startsWith("_olympiad?command")) {
                final int arenaId = Integer.parseInt(_command.split("=")[2]);
                final IBypassHandler handler = BypassHandler.getInstance().getHandler("arenachange");
                if (handler != null) {
                    handler.useBypass("arenachange " + (arenaId - 1), activeChar, null);
                }
            } else if (_command.startsWith("menu_select")) {
                final L2Npc lastNpc = activeChar.getLastFolkNPC();
                if ((lastNpc != null) && lastNpc.canInteract(activeChar)) {
                    final String[] split = _command.substring(_command.indexOf("?") + 1).split("&");
                    final int ask = Integer.parseInt(split[0].split("=")[1]);
                    final int reply = Integer.parseInt(split[1].split("=")[1]);
                    EventDispatcher.getInstance().notifyEventAsync(new OnNpcMenuSelect(activeChar, lastNpc, ask, reply), lastNpc);
                }
            } else if (_command.startsWith("manor_menu_select")) {
                final L2Npc lastNpc = activeChar.getLastFolkNPC();
                if (Config.ALLOW_MANOR && (lastNpc != null) && lastNpc.canInteract(activeChar)) {
                    final String[] split = _command.substring(_command.indexOf("?") + 1).split("&");
                    final int ask = Integer.parseInt(split[0].split("=")[1]);
                    final int state = Integer.parseInt(split[1].split("=")[1]);
                    final boolean time = split[2].split("=")[1].equals("1");
                    EventDispatcher.getInstance().notifyEventAsync(new OnNpcManorBypass(activeChar, lastNpc, ask, state, time), lastNpc);
                }
            } else if (_command.startsWith("pccafe")) {
                final L2PcInstance player = client.getActiveChar();
                if ((player == null) || !Config.PC_CAFE_ENABLED) {
                    return;
                }
                final int multisellId = Integer.parseInt(_command.substring(10).trim());
                MultisellData.getInstance().separateAndSend(multisellId, activeChar, null, false);
            } else {
                final IBypassHandler handler = BypassHandler.getInstance().getHandler(_command);
                if (handler != null) {
                    if (bypassOriginId > 0) {
                        final L2Object bypassOrigin = L2World.getInstance().findObject(bypassOriginId);
                        if ((bypassOrigin != null) && bypassOrigin.isCharacter()) {
                            handler.useBypass(_command, activeChar, (L2Character) bypassOrigin);
                        } else {
                            handler.useBypass(_command, activeChar, null);
                        }
                    } else {
                        handler.useBypass(_command, activeChar, null);
                    }
                } else {
                    LOGGER.warn(client + " sent not handled RequestBypassToServer: [" + _command + "]");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Exception processing bypass from player " + activeChar.getName() + ": " + _command, e);

            if (activeChar.isGM()) {
                final StringBuilder sb = new StringBuilder(200);
                sb.append("<html><body>");
                sb.append("Bypass error: " + e + "<br1>");
                sb.append("Bypass command: " + _command + "<br1>");
                sb.append("StackTrace:<br1>");
                for (StackTraceElement ste : e.getStackTrace()) {
                    sb.append(ste + "<br1>");
                }
                sb.append("</body></html>");
                // item html
                final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1, sb.toString());
                msg.disableValidation();
                activeChar.sendPacket(msg);
            }
        }
    }
}
