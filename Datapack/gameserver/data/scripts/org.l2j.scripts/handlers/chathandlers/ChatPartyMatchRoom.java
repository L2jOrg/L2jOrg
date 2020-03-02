package handlers.chathandlers;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.CreatureSay;

import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * Party Match Room chat handler.
 * @author Gnacik
 * @author JoeAlisson
 */
public class ChatPartyMatchRoom implements IChatHandler {

	private static final ChatType[] CHAT_TYPES = {
		ChatType.PARTYMATCH_ROOM,
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		doIfNonNull(player.getMatchingRoom(), room -> {
			final CreatureSay cs = new CreatureSay(player, type, text);
			room.getMembers().forEach(cs::sendTo);
		});
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}