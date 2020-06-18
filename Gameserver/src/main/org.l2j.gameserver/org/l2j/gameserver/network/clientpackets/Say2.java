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

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.ChatHandler;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerChat;
import org.l2j.gameserver.model.events.returns.ChatFilterReturn;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author JoeAlisson
 */
public final class Say2 extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(Say2.class);
    private static final Logger LOGGER_CHAT = LoggerFactory.getLogger("chat");

    private String text;
    private int type;
    private String target;

    @Override
    public void readImpl() {
        text = readString();
        type = readInt();
        if(type == ChatType.WHISPER.getClientId()) {
            readByte();
            target = readString();
        }
    }

    @Override
    public void runImpl() {
        var player = client.getPlayer();

        ChatType chatType = ChatType.findByClientId(type);

        if (isNull(chatType)) {
            LOGGER.warn("player {} send invalid type {} with text {}", player, type, text);
            Disconnection.of(player).defaultSequence(false);
            return;
        }

        if (text.isEmpty()) {
            LOGGER.warn("{} sending empty text. Possible packet hack!", player);
            Disconnection.of(player).defaultSequence(false);
            return;
        }

        // Even though the client can handle more characters than it's current limit allows, an overflow (critical error) happens if you pass a huge (1000+) message.
        // July 11, 2011 - Verified on High Five 4 official client as 105.
        // Allow higher limit if player shift some item (text is longer then).
        if (!player.isGM() &&  ( ( text.indexOf(8) >= 0 && text.length() > 500) || ( text.indexOf(8) < 0 && text.length() > 105 ))) {
            player.sendPacket(SystemMessageId.WHEN_A_USER_S_KEYBOARD_INPUT_EXCEEDS_A_CERTAIN_CUMULATIVE_SCORE_A_CHAT_BAN_WILL_BE_APPLIED_THIS_IS_DONE_TO_DISCOURAGE_SPAMMING_PLEASE_AVOID_POSTING_THE_SAME_MESSAGE_MULTIPLE_TIMES_DURING_A_SHORT_PERIOD);
            return;
        }

        var chatSettings = getSettings(ChatSettings.class);
        if (chatType == ChatType.WHISPER && chatSettings.l2WalkerProtectionEnabled() && chatSettings.isL2WalkerCommand(text)) {
            GameUtils.handleIllegalPlayerAction(player, "Client Emulator Detect: Player " + player + " using l2walker.");
            return;
        }

        if (player.isChatBanned() && text.charAt(0) != '.') {
            if (player.isAffected(EffectFlag.CHAT_BLOCK)) {
                player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_CHATTING_IS_NOT_ALLOWED);
            } else if (chatSettings.bannableChannels().contains(chatType)) {
                player.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER);
            }
            return;
        }

        if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CHAT_WHILE_PARTICIPATING_IN_THE_OLYMPIAD);
            return;
        }

        if (player.isJailed() && getSettings(GeneralSettings.class).disableChatInJail() && !player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
            player.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
            return;
        }

        if ((chatType == ChatType.PETITION_PLAYER) && player.isGM()) {
            chatType = ChatType.PETITION_GM;
        }

        if (chatSettings.logChat()) {
            if (chatType == ChatType.WHISPER) {
                LOGGER_CHAT.info("{} [ {} to {} ] {}", chatType, player,  target, text);
            } else {
                LOGGER_CHAT.info("{} [ {} ] {}", chatType, player, text);
            }

        }

        if (text.indexOf(8) >= 0) {
            if (!parseAndPublishItem(player)) {
                return;
            }
        }

        final ChatFilterReturn filter = EventDispatcher.getInstance().notifyEvent(new OnPlayerChat(player, target, text, chatType), player, ChatFilterReturn.class);
        if (nonNull(filter))  {
            text = filter.getFilteredText();
            chatType = filter.getChatType();
        }

        // Say Filter implementation
        if (chatSettings.enableChatFilter()) {
            text = chatSettings.filterText(text);
        }

        final IChatHandler handler = ChatHandler.getInstance().getHandler(chatType);
        if (nonNull(handler)) {
            handler.handleChat(chatType, player, target, text);
        } else {
            LOGGER.info("No handler registered for ChatType: " + type + " Player: " + client);
        }
    }

    private boolean parseAndPublishItem(Player owner) {
        int pos1 = -1;
        while ( (pos1 = text.indexOf(8, pos1)) > -1) {
            int pos = text.indexOf("ID=", pos1);
            if (pos == -1) {
                return false;
            }
            final StringBuilder result = new StringBuilder(9);
            pos += 3;
            while (Character.isDigit(text.charAt(pos))) {
                result.append(text.charAt(pos++));
            }
            final int id = Integer.parseInt(result.toString());
            final Item item = owner.getInventory().getItemByObjectId(id);

            if (isNull(item)) {
                LOGGER.warn("{} trying publish item which doesnt own! ID: {}", owner, id);
                return false;
            }
            item.publish();

            pos1 = text.indexOf(8, pos) + 1;
            if (pos1 == 0) // missing ending tag
            {
                LOGGER.warn("{} sent invalid publish item msg! ID:{}", owner, id);
                return false;
            }
        }
        return true;
    }
}
