package handler.voicecommands;

import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.htm.HtmTemplates;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.gs2as.ChangePhoneNumber;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.utils.Functions;

/**
 * @author Bonux
**/
public class PhoneNumber extends ScriptVoiceCommandHandler
{
	private class Listener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			if(!Config.FORCIBLY_SPECIFY_PHONE_NUMBER)
				return;

			GameClient connection = player.getNetConnection();
			if(connection == null)
				return;

			long currentNumber = connection.getPhoneNumber();
			if(currentNumber > 0)
				return;

			useVoicedCommand("telephone", player, null);
		}
	}

	private final String[] COMMANDS = new String[] { "telephone", "phonenumber", "phone" };

	private final String PHONE_REGEX = "^\\+?([87](?!95[4-79]|99[08]|907|94[^0]|336)([348]\\d|9[0-6789]|7[0247])\\d{8}|[1246]\\d{9,13}|68\\d{7}|5[1-46-9]\\d{8,12}|55[1-9]\\d{9}|55[12]19\\d{8}|500[56]\\d{4}|5016\\d{6}|5068\\d{7}|502[45]\\d{7}|5037\\d{7}|50[4567]\\d{8}|50855\\d{4}|509[34]\\d{7}|376\\d{6}|855\\d{8}|856\\d{10}|85[0-4789]\\d{8,10}|8[68]\\d{10,11}|8[14]\\d{10}|82\\d{9,10}|852\\d{8}|90\\d{10}|96(0[79]|17[01]|13)\\d{6}|96[23]\\d{9}|964\\d{10}|96(5[69]|89)\\d{7}|96(65|77)\\d{8}|92[023]\\d{9}|91[1879]\\d{9}|9[34]7\\d{8}|959\\d{7}|989\\d{9}|97\\d{8,12}|99[^4568]\\d{7,11}|994\\d{9}|9955\\d{8}|996[57]\\d{8}|9989\\d{8}|380[3-79]\\d{8}|381\\d{9}|385\\d{8,9}|375[234]\\d{8}|372\\d{7,8}|37[0-4]\\d{8}|37[6-9]\\d{7,11}|30[69]\\d{9}|34[67]\\d{8}|3[12359]\\d{8,12}|36\\d{9}|38[1679]\\d{8}|382\\d{8,9}|46719\\d{10})$";

	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		if(!Config.ALLOW_CHANGE_PHONE_NUMBER_COMMAND)
			return false;

		GameClient connection = player.getNetConnection();
		if(connection == null)
			return false;

		HtmTemplates tpls = HtmCache.getInstance().getTemplates("command/phonenumber.htm", player);

		String html = tpls.get(0);
		long currentNumber = connection.getPhoneNumber();

		String msg = null;
		if(args != null && !args.isEmpty())
		{
			if(!args.matches(PHONE_REGEX))
				msg = tpls.get(3);
			else if(AuthServerCommunication.getInstance().isShutdown())
				msg = tpls.get(4);
			else
			{
				long newNumber = Long.parseLong(args.replaceAll("[^0-9]", ""));
				if(newNumber == currentNumber)
					msg = tpls.get(5);
				else
				{
					currentNumber = newNumber;
					msg = tpls.get(6);
					connection.setPhoneNumber(currentNumber);
					AuthServerCommunication.getInstance().sendPacket(new ChangePhoneNumber(player.getAccountName(), currentNumber));
				}
			}
		}

		html = html.replace("<?current_number?>", currentNumber > 0 ? ("+" + String.valueOf(currentNumber)) : tpls.get(1));

		if(msg != null)
		{
			String msgBlock = tpls.get(2);
			msgBlock = msgBlock.replace("<?message_text?>", msg);
			html = html.replace("<?message?>", msgBlock);
		}

		Functions.show(html, player);
		return true;
	}

	@Override
	public void onInit()
	{
		super.onInit();

		if(Config.FORCIBLY_SPECIFY_PHONE_NUMBER)
			CharListenerList.addGlobal(new Listener());
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}
