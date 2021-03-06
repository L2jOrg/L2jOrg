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
package org.l2j.scripts.handlers.chathandlers;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.CreatureSay;

/**
 * Party Room All chat handler.
 * @author durgus
 * @author JoeAlisson
 */
public final class ChatPartyRoomAll implements IChatHandler {

	private static final ChatType[] CHAT_TYPES = {
		ChatType.PARTYROOM_ALL,
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		if (player.isInParty()) {
			if (player.getParty().isInCommandChannel() && player.getParty().isLeader(player)) {
				player.getParty().getCommandChannel().broadcastCreatureSay(new CreatureSay(player, type, text), player);
			}
		}
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}