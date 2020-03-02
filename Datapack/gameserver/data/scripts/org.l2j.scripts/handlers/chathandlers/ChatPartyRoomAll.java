package handlers.chathandlers;

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