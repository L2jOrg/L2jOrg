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
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.world.World;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Trade chat handler.
 * @author durgus
 * @author JoeAlisson
 */
public final class ChatTrade implements IChatHandler {

	private static final ChatType[] CHAT_TYPES = {
		ChatType.TRADE,
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		var chatSettings = getSettings(ChatSettings.class);
		var levelRequired =  chatSettings.tradeChatLevel();

		if (player.getLevel() < levelRequired) {
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.TRADE_CHAT_CANNOT_BE_USED_BY_USERS_LV_S1_OR_LOWER).addInt(levelRequired));
			return;
		}
		
		final CreatureSay cs = new CreatureSay(player, type, text);

		if (chatSettings.defaultTradeChat().equalsIgnoreCase("on") || (chatSettings.defaultTradeChat().equalsIgnoreCase("gm") && player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS))) {
			final int region = MapRegionManager.getInstance().getMapRegionLocId(player);
			World.getInstance().forEachPlayer(receiver -> {
				if ((region == MapRegionManager.getInstance().getMapRegionLocId(receiver)) && !BlockList.isBlocked(receiver, player) && (receiver.getInstanceId() == player.getInstanceId())) {
					receiver.sendPacket(cs);
				}
			});
		}
		else if (chatSettings.defaultTradeChat().equalsIgnoreCase("global")) {
			if (!player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS) && !player.getFloodProtectors().getGlobalChat().tryPerformAction("global chat")) {
				player.sendMessage("Do not spam trade channel.");
				return;
			}

			World.getInstance().forEachPlayer(receiver -> {
				if (!BlockList.isBlocked(receiver, player)) {
					receiver.sendPacket(cs);
				}
			});
		}
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}