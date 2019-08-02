/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.chathandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.settings.GeneralSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Trade chat handler.
 * @author durgus
 */
public final class ChatTrade implements IChatHandler
{
	private static final ChatType[] CHAT_TYPES =
	{
		ChatType.TRADE,
	};
	
	@Override
	public void handleChat(ChatType type, Player activeChar, String target, String text)
	{
		if (activeChar.isChatBanned() && Config.BAN_CHAT_CHANNELS.contains(type))
		{
			activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER);
			return;
		}
		if (Config.JAIL_DISABLE_CHAT && activeChar.isJailed() && !activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS))
		{
			activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
			return;
		}
		var levelRequired =  getSettings(GeneralSettings.class).tradeChatLevel();
		if (activeChar.getLevel() < levelRequired) {
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.TRADE_CHAT_CANNOT_BE_USED_BY_NON_PREMIUM_USERS_LV_S1_OR_LOWER).addInt(levelRequired));
			return;
		}
		
		final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		if (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("on") || (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("gm") && activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)))
		{
			final int region = MapRegionManager.getInstance().getMapRegionLocId(activeChar);
			for (Player player : World.getInstance().getPlayers())
			{
				if ((region == MapRegionManager.getInstance().getMapRegionLocId(player)) && !BlockList.isBlocked(player, activeChar) && (player.getInstanceId() == activeChar.getInstanceId()))
				{
					player.sendPacket(cs);
				}
			}
		}
		else if (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("global"))
		{
			if (!activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS) && !activeChar.getFloodProtectors().getGlobalChat().tryPerformAction("global chat"))
			{
				activeChar.sendMessage("Do not spam trade channel.");
				return;
			}
			
			for (Player player : World.getInstance().getPlayers())
			{
				if (!BlockList.isBlocked(player, activeChar))
				{

					player.sendPacket(cs);

				}
			}
		}
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}