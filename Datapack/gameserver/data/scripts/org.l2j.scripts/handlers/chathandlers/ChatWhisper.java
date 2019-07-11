package handlers.chathandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.settings.GeneralSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Tell chat handler.
 * @author durgus
 */
public final class ChatWhisper implements IChatHandler {

	private static final ChatType[] CHAT_TYPES = {
		ChatType.WHISPER
	};
	
	@Override
	public void handleChat(ChatType type, Player activeChar, String target, String text) {
		if (activeChar.isChatBanned() && Config.BAN_CHAT_CHANNELS.contains(type)) {
			activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER);
			return;
		}
		
		if (Config.JAIL_DISABLE_CHAT && activeChar.isJailed() && !activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
			activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
			return;
		}
		
		// Return if no target is set
		if (target == null) {
			return;
		}
		
		final Player receiver = L2World.getInstance().getPlayer(target);
		
		if ((receiver != null) && !receiver.isSilenceMode(activeChar.getObjectId())) {

			if (Config.JAIL_DISABLE_CHAT && receiver.isJailed() && !activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
				activeChar.sendMessage("Player is in jail.");
				return;
			}
			if (receiver.isChatBanned()) {
				activeChar.sendPacket(SystemMessageId.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
				return;
			}
			if ((receiver.getClient() == null) || receiver.getClient().isDetached()) {
				activeChar.sendMessage("Player is in offline mode.");
				return;
			}
			var levelRequired = getSettings(GeneralSettings.class).whisperChatLevel();
			if ((activeChar.getLevel() < levelRequired) && !activeChar.getWhisperers().contains(receiver.getObjectId()) && !activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NON_PREMIUM_USERS_LV_S1_OR_LOWER_CAN_RESPOND_TO_A_WHISPER_BUT_CANNOT_INITIATE_IT).addInt(levelRequired));
				return;
			}
			if (!BlockList.isBlocked(receiver, activeChar)) {
				// Allow reciever to send PMs to this char, which is in silence mode.
				if (Config.SILENCE_MODE_EXCLUDE && activeChar.isSilenceMode()) {
					activeChar.addSilenceModeExcluded(receiver.getObjectId());
				}
				
				receiver.getWhisperers().add(activeChar.getObjectId());
				receiver.sendPacket(new CreatureSay(activeChar, receiver, activeChar.getName(), type, text));
				activeChar.sendPacket(new CreatureSay(activeChar, receiver, "->" + receiver.getName(), type, text));
			} else {
				activeChar.sendPacket(SystemMessageId.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
			}
		} else {
			activeChar.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
		}
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}