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
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * Tell chat handler.
 * @author durgus
 * @author JoeAlisson
 */
public final class ChatWhisper implements IChatHandler {

	private static final ChatType[] CHAT_TYPES = {
		ChatType.WHISPER
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		if (isNullOrEmpty(target)) {
			return;
		}
		
		final Player receiver = World.getInstance().findPlayer(target);
		
		if ((receiver != null) && !receiver.isSilenceMode(player.getObjectId())) {
			if (receiver.isChatBanned()) {
				player.sendPacket(SystemMessageId.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
				return;
			}
			if (isNull(receiver.getClient()) || receiver.getClient().isDetached()) {
				player.sendMessage("Player is in offline mode.");
				return;
			}

			var chatSettings = getSettings(ChatSettings.class);
			var levelRequired = chatSettings.whisperChatLevel();
			if ((player.getLevel() < levelRequired) && !player.getWhisperers().contains(receiver.getObjectId()) && !player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.USERS_LV_S1_OR_LOWER_CAN_RESPOND_TO_A_WHISPER_BUT_CANNOT_INITIATE_IT).addInt(levelRequired));
				return;
			}

			if (!BlockList.isBlocked(receiver, player)) {
				// Allow receiver to send PMs to this char, which is in silence mode.
				if (chatSettings.silenceModeExclude() && player.isSilenceMode()) {
					player.addSilenceModeExcluded(receiver.getObjectId());
				}
				
				receiver.getWhisperers().add(player.getObjectId());
				receiver.sendPacket(new CreatureSay(player, receiver, player.getAppearance().getVisibleName(), type, text));
				player.sendPacket(new CreatureSay(player, receiver, "->" + receiver.getAppearance().getVisibleName(), type, text));
			} else {
				player.sendPacket(SystemMessageId.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
			}
		} else {
			player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
		}
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}