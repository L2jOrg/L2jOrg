package handlers.chathandlers;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.CreatureSay;

/**
 * Party Room Commander chat handler.
 * @author durgus
 */
public final class ChatPartyRoomCommander implements IChatHandler {
	private static final ChatType[] CHAT_TYPES = {
		ChatType.PARTYROOM_COMMANDER,
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		if (player.isInParty()) {
			if (player.getParty().isInCommandChannel() && player.getParty().getCommandChannel().getLeader().equals(player)) {
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