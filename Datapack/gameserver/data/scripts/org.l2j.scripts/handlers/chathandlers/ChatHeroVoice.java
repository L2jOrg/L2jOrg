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
import org.l2j.gameserver.world.World;

/**
 * Hero chat handler.
 * @author durgus
 * @author JoeAlisson
 */
public final class ChatHeroVoice implements IChatHandler {

	private static final ChatType[] CHAT_TYPES = {
		ChatType.HERO_VOICE,
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		if (!player.isHero() && !player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
			player.sendPacket(SystemMessageId.ONLY_HEROES_CAN_ENTER_THE_HERO_CHANNEL);
			return;
		}

		if (!player.getFloodProtectors().getHeroVoice().tryPerformAction("hero voice")) {
			player.sendMessage("Action failed. Heroes are only able to speak in the global channel once every 10 seconds.");
			return;
		}
		
		final CreatureSay cs = new CreatureSay(player, type, text);
		World.getInstance().forEachPlayer(receiver -> {
			if(!BlockList.isBlocked(receiver, player)) {
				receiver.sendPacket(cs);
			}
		});
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}