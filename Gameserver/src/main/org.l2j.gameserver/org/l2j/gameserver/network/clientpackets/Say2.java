package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.ChatHandler;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerChat;
import org.l2j.gameserver.model.events.returns.ChatFilterReturn;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class ...
 *
 * @version $Revision: 1.16.2.12.2.7 $ $Date: 2005/04/11 10:06:11 $
 */
public final class Say2 extends ClientPacket {
    private static final String[] WALKER_COMMAND_LIST =
            {
                    "USESKILL",
                    "USEITEM",
                    "BUYITEM",
                    "SELLITEM",
                    "SAVEITEM",
                    "LOADITEM",
                    "MSG",
                    "DELAY",
                    "LABEL",
                    "JMP",
                    "CALL",
                    "RETURN",
                    "MOVETO",
                    "NPCSEL",
                    "NPCDLG",
                    "DLGSEL",
                    "CHARSTATUS",
                    "POSOUTRANGE",
                    "POSINRANGE",
                    "GOHOME",
                    "SAY",
                    "EXIT",
                    "PAUSE",
                    "STRINDLG",
                    "STRNOTINDLG",
                    "CHANGEWAITTYPE",
                    "FORCEATTACK",
                    "ISMEMBER",
                    "REQUESTJOINPARTY",
                    "REQUESTOUTPARTY",
                    "QUITPARTY",
                    "MEMBERSTATUS",
                    "CHARBUFFS",
                    "ITEMCOUNT",
                    "FOLLOWTELEPORT"
            };
    private static Logger LOGGER = LoggerFactory.getLogger(Say2.class);
    private static Logger LOGGER_CHAT = LoggerFactory.getLogger("chat");
    private String _text;
    private int _type;
    private String _target;

    @Override
    public void readImpl() {
        _text = readString();
        _type = readInt();
        _target = (_type == ChatType.WHISPER.getClientId()) ? readString() : null;
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        ChatType chatType = ChatType.findByClientId(_type);
        if (chatType == null) {
            LOGGER.warn("Say2: Invalid type: " + _type + " Player : " + player.getName() + " text: " + _text);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            Disconnection.of(player).defaultSequence(false);
            return;
        }

        if (_text.isEmpty()) {
            LOGGER.warn(player.getName() + ": sending empty text. Possible packet hack!");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            Disconnection.of(player).defaultSequence(false);
            return;
        }

        // Even though the client can handle more characters than it's current limit allows, an overflow (critical error) happens if you pass a huge (1000+) message.
        // July 11, 2011 - Verified on High Five 4 official client as 105.
        // Allow higher limit if player shift some item (text is longer then).
        if (!player.isGM() && (((_text.indexOf(8) >= 0) && (_text.length() > 500)) || ((_text.indexOf(8) < 0) && (_text.length() > 105)))) {
            player.sendPacket(SystemMessageId.WHEN_A_USER_S_KEYBOARD_INPUT_EXCEEDS_A_CERTAIN_CUMULATIVE_SCORE_A_CHAT_BAN_WILL_BE_APPLIED_THIS_IS_DONE_TO_DISCOURAGE_SPAMMING_PLEASE_AVOID_POSTING_THE_SAME_MESSAGE_MULTIPLE_TIMES_DURING_A_SHORT_PERIOD);
            return;
        }

        if (Config.L2WALKER_PROTECTION && (chatType == ChatType.WHISPER) && checkBot(_text)) {
            GameUtils.handleIllegalPlayerAction(player, "Client Emulator Detect: Player " + player.getName() + " using l2walker.", Config.DEFAULT_PUNISH);
            return;
        }

        if (player.isCursedWeaponEquipped() && ((chatType == ChatType.TRADE) || (chatType == ChatType.SHOUT))) {
            player.sendPacket(SystemMessageId.SHOUT_AND_TRADE_CHATTING_CANNOT_BE_USED_WHILE_POSSESSING_A_CURSED_WEAPON);
            return;
        }

        if (player.isChatBanned() && (_text.charAt(0) != '.')) {
            if (player.isAffected(EffectFlag.CHAT_BLOCK)) {
                player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_CHATTING_IS_NOT_ALLOWED);
            } else if (Config.BAN_CHAT_CHANNELS.contains(chatType)) {
                player.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER);
            }
            return;
        }

        if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CHAT_WHILE_PARTICIPATING_IN_THE_OLYMPIAD);
            return;
        }

        if (player.isOnEvent(CeremonyOfChaosEvent.class)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CHAT_IN_THE_CEREMONY_OF_CHAOS);
            return;
        }

        if (player.isJailed() && Config.JAIL_DISABLE_CHAT) {
            if ((chatType == ChatType.WHISPER) || (chatType == ChatType.SHOUT) || (chatType == ChatType.TRADE) || (chatType == ChatType.HERO_VOICE)) {
                player.sendMessage("You can not chat with players outside of the jail.");
                return;
            }
        }

        if ((chatType == ChatType.PETITION_PLAYER) && player.isGM()) {
            chatType = ChatType.PETITION_GM;
        }

        if (Config.LOG_CHAT) {
            if (chatType == ChatType.WHISPER) {
                LOGGER_CHAT.info(chatType.name() + " [" + player + " to " + _target + "] " + _text);
            } else {
                LOGGER_CHAT.info(chatType.name() + " [" + player + "] " + _text);
            }

        }

        if (_text.indexOf(8) >= 0) {
            if (!parseAndPublishItem(client, player)) {
                return;
            }
        }

        final ChatFilterReturn filter = EventDispatcher.getInstance().notifyEvent(new OnPlayerChat(player, _target, _text, chatType), player, ChatFilterReturn.class);
        if (filter != null) {
            _text = filter.getFilteredText();
            chatType = filter.getChatType();
        }

        // Say Filter implementation
        if (Config.USE_SAY_FILTER) {
            checkText();
        }

        final IChatHandler handler = ChatHandler.getInstance().getHandler(chatType);
        if (handler != null) {
            handler.handleChat(chatType, player, _target, _text);
        } else {
            LOGGER.info("No handler registered for ChatType: " + _type + " Player: " + client);
        }
    }

    private boolean checkBot(String text) {
        for (String botCommand : WALKER_COMMAND_LIST) {
            if (text.startsWith(botCommand)) {
                return true;
            }
        }
        return false;
    }

    private void checkText() {
        String filteredText = _text;
        for (String pattern : Config.FILTER_LIST) {
            filteredText = filteredText.replaceAll("(?i)" + pattern, Config.CHAT_FILTER_CHARS);
        }
        _text = filteredText;
    }

    private boolean parseAndPublishItem(GameClient client, Player owner) {
        int pos1 = -1;
        while ((pos1 = _text.indexOf(8, pos1)) > -1) {
            int pos = _text.indexOf("ID=", pos1);
            if (pos == -1) {
                return false;
            }
            final StringBuilder result = new StringBuilder(9);
            pos += 3;
            while (Character.isDigit(_text.charAt(pos))) {
                result.append(_text.charAt(pos++));
            }
            final int id = Integer.parseInt(result.toString());
            final Item item = owner.getInventory().getItemByObjectId(id);

            if (item == null) {
                LOGGER.info(client + " trying publish item which doesnt own! ID:" + id);
                return false;
            }
            item.publish();

            pos1 = _text.indexOf(8, pos) + 1;
            if (pos1 == 0) // missing ending tag
            {
                LOGGER.info(client + " sent invalid publish item msg! ID:" + id);
                return false;
            }
        }
        return true;
    }
}
