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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.CreatureSay;

import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * Party Match Room chat handler.
 * @author Gnacik
 * @author JoeAlisson
 */
public class ChatPartyMatchRoom implements IChatHandler {

	private static final ChatType[] CHAT_TYPES = {
		ChatType.PARTYMATCH_ROOM,
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		doIfNonNull(player.getMatchingRoom(), room -> {
			final CreatureSay cs = new CreatureSay(player, type, text);
			room.getMembers().forEach(cs::sendTo);
		});
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}