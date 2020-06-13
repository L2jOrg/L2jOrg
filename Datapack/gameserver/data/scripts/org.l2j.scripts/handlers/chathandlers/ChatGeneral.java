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
package handlers.chathandlers;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.handler.VoicedCommandHandler;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.world.World;

import java.util.StringTokenizer;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * General Chat Handler.
 * @author durgus
 */
public final class ChatGeneral implements IChatHandler {

	private static final int CHAT_RANGE = 1250;
	private static final ChatType[] CHAT_TYPES = {
		ChatType.GENERAL,
	};

	@Override
	public void handleChat(ChatType type, Player player, String params, String text) {
		boolean vcd_used = checkUseVoicedCommand(player, params, text);

		if (!vcd_used) {

			var levelRequired = getSettings(ChatSettings.class).generalChatLevel();

			if ((player.getLevel() < levelRequired) && !player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.GENERAL_CHAT_CANNOT_BE_USED_BY_USERS_LV_S1_OR_LOWER).addInt(levelRequired));
				return;
			}
			
			final CreatureSay cs = new CreatureSay(player, type, text);
			World.getInstance().forEachPlayerInRange(player, CHAT_RANGE, cs::sendTo, receiver -> !BlockList.isBlocked(receiver, player));
			player.sendPacket(cs);
		}
	}

	private boolean checkUseVoicedCommand(Player activeChar, String params, String text) {
		if (text.startsWith(".")) {
			final StringTokenizer st = new StringTokenizer(text);
			String command;

			if (st.countTokens() > 1) {
				command = st.nextToken().substring(1);
				params = text.substring(command.length() + 2);
			} else {
				command = text.substring(1);
			}

			var vch = VoicedCommandHandler.getInstance().getHandler(command);
			if (nonNull(vch)) {
				return vch.useVoicedCommand(command, activeChar, params);
			}
		}
		return false;
	}

	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}