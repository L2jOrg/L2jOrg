package handlers.chathandlers;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Petition chat handler.
 * @author durgus
 * @author JoeAlisson
 */
public final class ChatPetition implements IChatHandler {

	private static final ChatType[] CHAT_TYPES = {
		ChatType.PETITION_PLAYER,
		ChatType.PETITION_GM,
	};
	
	@Override
	public void handleChat(ChatType type, Player player, String target, String text) {
		if (!PetitionManager.getInstance().isPlayerInConsultation(player)) {
			player.sendPacket(SystemMessageId.YOU_ARE_CURRENTLY_NOT_IN_A_PETITION_CHAT);
			return;
		}
		PetitionManager.getInstance().sendActivePetitionMessage(player, text);
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}