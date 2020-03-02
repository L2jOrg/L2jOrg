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