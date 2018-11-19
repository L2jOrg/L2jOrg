package l2s.authserver.network.l2.c2s;

import l2s.authserver.Config;
import l2s.authserver.GameServerManager;
import l2s.authserver.accounts.Account;
import l2s.authserver.network.gamecomm.GameServer;
import l2s.authserver.network.gamecomm.GameServer.HostInfo;
import l2s.authserver.network.l2.L2LoginClient;
import l2s.authserver.network.l2.SessionKey;
import l2s.authserver.network.l2.s2c.LoginFail;
import l2s.authserver.network.l2.s2c.LoginFail.LoginFailReason;
import l2s.authserver.network.l2.s2c.PlayOk;
import l2s.authserver.network.l2.s2c.ServerList;

/**
 * Format: ddc
 * d: fist part of session id
 * d: second part of session id
 * c: ?
 */
public class RequestServerList extends L2LoginClientPacket
{
	private int _loginOkID1;
	private int _loginOkID2;
	private int _unk;

	@Override
	protected void readImpl()
	{
		_loginOkID1 = readD();
		_loginOkID2 = readD();
		_unk = readC();
	}

	@Override
	protected void runImpl()
	{
		L2LoginClient client = getClient();
		SessionKey skey = client.getSessionKey();
		if(skey == null || !skey.checkLoginPair(_loginOkID1, _loginOkID2))
		{
			client.close(LoginFailReason.REASON_ACCESS_FAILED);
			return;
		}

		int serversCount = 0;
		int serverId = -1;
		for(GameServer gs : GameServerManager.getInstance().getGameServers())
		{
			for(HostInfo host : gs.getHosts())
			{
				if(gs.isOnline())
					serverId = host.getId();

				serversCount++;
			}
		}

		if(Config.DONT_SEND_SERVER_LIST_IF_ONE_SERVER && serversCount == 1 && serverId > 0)
		{
			Account account = client.getAccount();
			GameServer gs = GameServerManager.getInstance().getGameServerById(serverId);
			if(gs == null || !gs.isAuthed() || (gs.isGmOnly() && account.getAccessLevel() < 100) || (gs.getOnline() >= gs.getMaxPlayers() && account.getAccessLevel() < 50))
			{
				client.close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
				return;
			}

			account.setLastServer(serverId);
			account.update();

			client.close(new PlayOk(skey, serverId));
		}
		else
			client.sendPacket(new ServerList(client.getAccount()));
	}
}