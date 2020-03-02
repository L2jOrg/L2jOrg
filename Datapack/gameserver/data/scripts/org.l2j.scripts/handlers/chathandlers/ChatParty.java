package handlers.chathandlers;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;

/**
 * Party chat handler.
 * @author durgus
 * @author JoeAlisson
 */
public final class ChatParty implements IChatHandler {
	private static final ChatType[] CHAT_TYPES = {
		ChatType.PARTY,
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		if (!player.isInParty()) {
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_A_PARTY);
			return;
		}

		player.getParty().broadcastCreatureSay(new CreatureSay(player, type, text), player);
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}