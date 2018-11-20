package org.l2j.gameserver.network.authcomm.as2gs;

import java.util.List;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.dao.HardwareLimitsDAO;
import org.l2j.gameserver.dao.PremiumAccountDAO;
import org.l2j.gameserver.data.htm.HtmCache;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;
import org.l2j.gameserver.network.authcomm.SessionKey;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerInGame;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.*;

public class PlayerAuthResponse extends ReceivablePacket
{
	private String account;
	private boolean authed;
	private int playOkId1;
	private int playOkId2;
	private int loginOkId1;
	private int loginOkId2;
	private int bonus;
	private int bonusExpire;
	private int points;
	private String hwid;
	private long phoneNumber;

	@Override
	public void readImpl()
	{
		account = readString();
		authed = readC() == 1;
		/*if(authed)
		{
			playOkId1 = readD();
			playOkId2 = readD();
			loginOkId1 = readD();
			loginOkId2 = readD();
			bonus = readD();
			bonusExpire = readD();
			points = readD();
			hwid = readString();
			if(getByteBuffer().hasRemaining())
				phoneNumber = readQ();
		}*/
	}

	@Override
	protected void runImpl()
	{
		/*SessionKey skey = new SessionKey(loginOkId1, loginOkId2, playOkId1, playOkId2);*/
		GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(account);
		if(client == null)
			return;

		if(authed /*&& client.getSessionKey().equals(skey)*/)
		{
			if(Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_IP > 0 && AuthServerCommunication.getInstance().getAuthedClient(account) == null)
			{
				boolean ignored = false;
				for(String ignoredIP : Config.MAX_ACTIVE_ACCOUNTS_IGNORED_IP)
				{
					if(ignoredIP.equalsIgnoreCase(client.getIpAddr()))
					{
						ignored = true;
						break;
					}
				}

				if(!ignored)
				{
					int limit = Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_IP;

					int[] limits = HardwareLimitsDAO.getInstance().select(client.getIpAddr());
					if(limits[1] == -1 || limits[1] > System.currentTimeMillis() / 1000L)
						limit += limits[0];

					List<GameClient> clients = AuthServerCommunication.getInstance().getAuthedClientsByHWID(client.getIpAddr());
					clients.add(client);
					for(GameClient c : clients)
					{
						int[] limitsByAccount = HardwareLimitsDAO.getInstance().select(c.getLogin());
						if(limitsByAccount[1] == -1 || limitsByAccount[1] > System.currentTimeMillis() / 1000L)
							limit += limitsByAccount[0];
					}
					int activeWindows = AuthServerCommunication.getInstance().getAuthedClientsByIP(client.getIpAddr()).size();
					if(activeWindows >= limit)
					{
						String html = HtmCache.getInstance().getCache("windows_limit_ip.htm", client.getLanguage());
						if(html != null)
						{
							html = html.replace("<?active_windows?>", String.valueOf(activeWindows));
							html = html.replace("<?windows_limit?>", String.valueOf(limit));
							client.close(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.NORMAL_WINDOW, html));
						}
						else
							client.close(LoginResultPacket.ACCESS_FAILED_TRY_LATER);

						return;
					}
				}
			}

			if(Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID > 0 && AuthServerCommunication.getInstance().getAuthedClient(account) == null)
			{
				int limit = Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID;

				int[] limits = HardwareLimitsDAO.getInstance().select(client.getHWID());
				if(limits[1] == -1 || limits[1] > System.currentTimeMillis() / 1000L)
					limit += limits[0];

				List<GameClient> clients = AuthServerCommunication.getInstance().getAuthedClientsByHWID(client.getHWID());
				clients.add(client);
				for(GameClient c : clients)
				{
					int[] limitsByAccount = HardwareLimitsDAO.getInstance().select(c.getLogin());
					if(limitsByAccount[1] == -1 || limitsByAccount[1] > System.currentTimeMillis() / 1000L)
						limit += limitsByAccount[0];
				}
				int activeWindows = clients.size() - 1;
				if(activeWindows >= limit)
				{
					String html = HtmCache.getInstance().getCache("windows_limit_hwid.htm", client.getLanguage());
					if(html != null)
					{
						html = html.replace("<?active_windows?>", String.valueOf(activeWindows));
						html = html.replace("<?windows_limit?>", String.valueOf(limit));
						client.close(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.NORMAL_WINDOW, html));
					}
					else
						client.close(LoginResultPacket.ACCESS_FAILED_TRY_LATER);

					return;
				}
			}

			if(Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_IP > 0 || Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID > 0)
				client.sendPacket(TutorialCloseHtmlPacket.STATIC);

			client.setAuthed(true);
			client.setState(GameClient.GameClientState.AUTHED);
			client.sendPacket(LoginResultPacket.SUCCESS);

			if(Config.PREMIUM_ACCOUNT_BASED_ON_GAMESERVER)
			{
				int[] bonuses = PremiumAccountDAO.getInstance().select(account);
				bonus = bonuses[0];
				bonusExpire = bonuses[1];
			}

			client.setPremiumAccountType(bonus);
			client.setPremiumAccountExpire(bonusExpire);
			client.setPoints(points);

			GameClient oldClient = AuthServerCommunication.getInstance().addAuthedClient(client);
			if(oldClient != null)
			{
				oldClient.setAuthed(false);
				Player activeChar = oldClient.getActiveChar();
				if(activeChar != null)
				{
					activeChar.sendPacket(SystemMsg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
					activeChar.logout();
				}
				else
				{
					oldClient.close(ServerCloseSocketPacket.STATIC);
				}
			}

			sendPacket(new PlayerInGame(client.getLogin()));

			CharacterSelectionInfoPacket csi = new CharacterSelectionInfoPacket(client);
			client.sendPacket(csi);
			client.setCharSelection(csi.getCharInfo());
			client.checkHwid(hwid);
			client.setPhoneNumber(phoneNumber);
		}
		else
		{
			client.close(LoginResultPacket.ACCESS_FAILED_TRY_LATER);
		}
	}
}