package l2s.authserver.network.gamecomm.gs2as;

import l2s.authserver.accounts.SessionManager;
import l2s.authserver.accounts.SessionManager.Session;
import l2s.authserver.network.gamecomm.ReceivablePacket;
import l2s.authserver.network.gamecomm.as2gs.PlayerAuthResponse;
import l2s.authserver.network.l2.SessionKey;

public class PlayerAuthRequest extends ReceivablePacket
{
	private String account;
	private int playOkId1;
	private int playOkId2;
	private int loginOkId1;
	private int loginOkId2;

	@Override
	protected void readImpl()
	{
		account = readS();
		playOkId1 = readD();
		playOkId2 = readD();
		loginOkId1 = readD();
		loginOkId2 = readD();
	}

	@Override
	protected void runImpl()
	{
		SessionKey skey = new SessionKey(loginOkId1, loginOkId2, playOkId1, playOkId2);

		Session session = SessionManager.getInstance().closeSession(skey);
		if(session == null || !session.getAccount().getLogin().equals(account))
		{
			sendPacket(new PlayerAuthResponse(account));
			return;
		}	
		sendPacket(new PlayerAuthResponse(session, session.getSessionKey().equals(skey))) ;
	}
}
