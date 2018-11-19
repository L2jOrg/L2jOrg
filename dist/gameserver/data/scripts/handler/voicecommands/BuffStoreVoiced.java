package handler.voicecommands;

import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.htm.HtmTemplates;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.Functions;

/**
 * Un voiced para poder setear buff stores
 *
 * @author Prims
 */
public class BuffStoreVoiced extends ScriptVoiceCommandHandler
{
	private static final String[] COMMANDS = { "buffstore" };

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		try
		{
			// Check if the player can set a store
			if(!Config.BUFF_STORE_ALLOWED_CLASS_LIST.contains(activeChar.getClassId().getId()))
			{
				activeChar.sendMessage("Your profession is not allowed to set an Buff Store");
				return false;
			}

			HtmTemplates tpls = HtmCache.getInstance().getTemplates("command/buffstore/buff_store.htm", activeChar);

			String html = tpls.get(0);
			html = html.replace("<?button?>", activeChar.isInBuffStore() ? tpls.get(1) : tpls.get(2));
			Functions.show(html, activeChar);
		}
		catch (Exception e)
		{
			activeChar.sendMessage("Use: .buffstore");
		}
		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}
