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

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.ExWorldChatCnt;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.world.World;

import java.time.Duration;
import java.time.Instant;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * World chat handler.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class ChatWorld implements IChatHandler {
	private static final IntMap<Instant> REUSE = new CHashIntMap<>();

	private static final ChatType[] CHAT_TYPES = {
		ChatType.WORLD,
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		var chatSettings = getSettings(ChatSettings.class);
		if (!chatSettings.worldChatEnabled()) {
			return;
		}
		
		final Instant now = Instant.now();
		if (!REUSE.isEmpty()) {
			REUSE.values().removeIf(now::isAfter);
		}
		
		if (player.getLevel() < chatSettings.worldChatMinLevel() && player.getVipTier() < 1) {
			player.sendPacket(getSystemMessage(SystemMessageId.YOU_MUST_BE_LV_S1_OR_HIGHER_TO_USE_WORLD_CHAT_YOU_CAN_ALSO_USE_IT_WITH_VIP_BENEFITS).addInt(chatSettings.worldChatMinLevel()));
		}
		else if (player.getWorldChatUsed() >= player.getWorldChatPoints()) {
			player.sendPacket(SystemMessageId.YOU_USED_WORLD_CHAT_UP_TO_TODAY_S_LIMIT_THE_USAGE_COUNT_OF_WORLD_CHAT_IS_RESET_EVERY_DAY_AT_6_30);
		}
		else {
			// Verify if player is not spaming.
			if (chatSettings.worldChatInterval().getSeconds() > 0)
			{
				final Instant instant = REUSE.getOrDefault(player.getObjectId(), null);
				if (nonNull(instant) && instant.isAfter(now))
				{
					final Duration timeDiff = Duration.between(now, instant);
					player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_S1_SEC_UNTIL_YOU_ARE_ABLE_TO_USE_WORLD_CHAT).addInt((int) timeDiff.getSeconds()));
					return;
				}
			}
			
			final CreatureSay cs = new CreatureSay(player, type, text);
			World.getInstance().getPlayers().stream().filter(player::isNotBlocked).forEach(cs::sendTo);
			
			player.setWorldChatUsed(player.getWorldChatUsed() + 1);
			player.sendPacket(new ExWorldChatCnt(player));
			if (chatSettings.worldChatInterval().getSeconds() > 0) {
				REUSE.put(player.getObjectId(), now.plus(chatSettings.worldChatInterval()));
			}
		}
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}