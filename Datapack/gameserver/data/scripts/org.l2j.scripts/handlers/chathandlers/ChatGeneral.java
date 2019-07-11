package handlers.chathandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.handler.VoicedCommandHandler;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.settings.GeneralSettings;

import java.util.StringTokenizer;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * General Chat Handler.
 * @author durgus
 */
public final class ChatGeneral implements IChatHandler {

	private static final ChatType[] CHAT_TYPES = {
		ChatType.GENERAL,
	};
	
	@Override
	public void handleChat(ChatType type, Player activeChar, String params, String text) {
		boolean vcd_used = false;
		if (text.startsWith(".")) {
			final StringTokenizer st = new StringTokenizer(text);
			final IVoicedCommandHandler vch;
			String command;
			
			if (st.countTokens() > 1) {
				command = st.nextToken().substring(1);
				params = text.substring(command.length() + 2);
			} else {
				command = text.substring(1);
			}
			vch = VoicedCommandHandler.getInstance().getHandler(command);
			if (vch != null) {
				vch.useVoicedCommand(command, activeChar, params);
				vcd_used = true;
			}
		}
		
		if (!vcd_used) {
			if (activeChar.isChatBanned() && Config.BAN_CHAT_CHANNELS.contains(type)) {
				activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER);
				return;
			}

			var levelRequired = getSettings(GeneralSettings.class).generalChatLevel();

			if ((activeChar.getLevel() < levelRequired) && !activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.GENERAL_CHAT_CANNOT_BE_USED_BY_NON_PREMIUM_USERS_LV_S1_OR_LOWER).addInt(levelRequired));
				return;
			}
			
			final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getAppearance().getVisibleName(), text);
			L2World.getInstance().forEachVisibleObjectInRange(activeChar, Player.class, 1250, player -> {
				if ((player != null) && !BlockList.isBlocked(player, activeChar))
				{
					player.sendPacket(cs);
				}
			});
			
			activeChar.sendPacket(cs);
		}
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}