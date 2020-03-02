package handlers.chathandlers;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;

import static java.util.Objects.isNull;

/**
 * Clan chat handler
 * @author durgus
 * @author JoeAlisson
 */
public final class ChatClan implements IChatHandler {

	private static final ChatType[] CHAT_TYPES = {
		ChatType.CLAN,
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		if (isNull(player.getClan())) {
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_A_CLAN);
			return;
		}

		player.getClan().broadcastCSToOnlineMembers(new CreatureSay(player, type, text), player);
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}