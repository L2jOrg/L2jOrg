package handlers.chathandlers;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;

/**
 * Alliance Chat Handler.
 * @author JoeAlisson
 */
public final class ChatAlliance implements IChatHandler {

	private static final ChatType[] CHAT_TYPES = {
		ChatType.ALLIANCE,
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		if (player.getAllyId() == 0) {
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_AN_ALLIANCE);
			return;
		}

		player.getClan().broadcastToOnlineAllyMembers(new CreatureSay(player, type, text));
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}