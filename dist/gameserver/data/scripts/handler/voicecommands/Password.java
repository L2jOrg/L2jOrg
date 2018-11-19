package handler.voicecommands;

import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.htm.HtmTemplates;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.gs2as.ChangePassword;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Util;

/**
 * @author Bonux
**/
public class Password extends ScriptVoiceCommandHandler
{
	private final String[] COMMANDS = new String[] { "password", "pswd" };

	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		if(!Config.ALLOW_CHANGE_PASSWORD_COMMAND)
			return false;

		HtmTemplates tpls = HtmCache.getInstance().getTemplates("command/password.htm", player);

		String html = tpls.get(0);

		String msg = null;
		if(args != null && !args.isEmpty())
		{
			if(AuthServerCommunication.getInstance().isShutdown())
				msg = tpls.get(2);
			else
			{
				String[] parts = args.split(" ");
				if(parts.length != 3)
					msg = tpls.get(6);
				else if(!parts[1].equals(parts[2]))
					msg = tpls.get(4);
				else if(parts[1].equals(parts[0]))
					msg = tpls.get(7);
				else if(parts[1].length() < 5 || parts[1].length() > 20)
					msg = tpls.get(5);
				else if(!Util.isMatchingRegexp(parts[1], Config.APASSWD_TEMPLATE))
					msg = tpls.get(3);
				else
				{
					msg = tpls.get(8);
					AuthServerCommunication.getInstance().sendPacket(new ChangePassword(player.getAccountName(), parts[0], parts[1], player.getHWID()));
				}
			}
		}

		if(msg != null)
		{
			String msgBlock = tpls.get(1);
			msgBlock = msgBlock.replace("<?message_text?>", msg);
			html = html.replace("<?message?>", msgBlock);
		}

		Functions.show(html, player);
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}
