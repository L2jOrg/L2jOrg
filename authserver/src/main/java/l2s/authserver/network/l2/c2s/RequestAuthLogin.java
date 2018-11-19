package l2s.authserver.network.l2.c2s;

import javax.crypto.Cipher;

import l2s.authserver.Config;
import l2s.authserver.GameServerManager;
import l2s.authserver.IpBanManager;
import l2s.authserver.accounts.Account;
import l2s.authserver.accounts.SessionManager;
import l2s.authserver.accounts.SessionManager.Session;
import l2s.authserver.crypt.PasswordHash;
import l2s.authserver.network.gamecomm.GameServer;
import l2s.authserver.network.gamecomm.as2gs.GetAccountInfo;
import l2s.authserver.network.l2.L2LoginClient;
import l2s.authserver.network.l2.L2LoginClient.LoginClientState;
import l2s.authserver.network.l2.s2c.LoginFail.LoginFailReason;
import l2s.authserver.network.l2.s2c.LoginOk;
import l2s.authserver.utils.Log;

/**
 * Format: b[128]ddddddhc
 * b[128]: the rsa encrypted block with the login an password
 */
public class RequestAuthLogin extends L2LoginClientPacket
{
	private final byte[] _raw1 = new byte[128];
	private final byte[] _raw2 = new byte[128];
	private boolean _newAuthMethod = false;

	@Override
	protected void readImpl()
	{
		if(_buf.remaining() >= (_raw1.length + _raw2.length))
		{
			_newAuthMethod = true;
			readB(_raw1);
			readB(_raw2);
		}

		if(_buf.remaining() >= _raw1.length)
		{
			readB(_raw1);
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readH();
			readC();
		}
	}

	@Override
	protected void runImpl() throws Exception
	{
		L2LoginClient client = getClient();

		byte[] decUser = null;
		byte[] decPass = null;
		try
		{
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
			decUser = rsaCipher.doFinal(_raw1, 0x00, 0x80);

			if(_newAuthMethod)
				decPass = rsaCipher.doFinal(_raw2, 0x00, _raw2.length);
		}
		catch(Exception e)
		{
			client.closeNow(true);
			return;
		}

		String user = null;
		String password = null;
		if(_newAuthMethod)
		{
			user = new String(decUser, 0x4E, 32).trim().toLowerCase();
			password = new String(decPass, 0x5C, 16).trim();
		}
		else
		{
			user = new String(decUser, 0x5E, 14).trim().toLowerCase();
			password = new String(decUser, 0x6C, 16).trim();
		}

		/*int ncotp = decrypted[0x7c];
		ncotp |= decrypted[0x7d] << 8;
		ncotp |= decrypted[0x7e] << 16;
		ncotp |= decrypted[0x7f] << 24;*/

		int currentTime = (int) (System.currentTimeMillis() / 1000L);

		Account account = new Account(user);
		account.restore();

		String passwordHash = Config.DEFAULT_CRYPT.encrypt(password);

		if(account.getPasswordHash() == null)
		{
			if(Config.AUTO_CREATE_ACCOUNTS && user.matches(Config.ANAME_TEMPLATE) && password.matches(Config.APASSWD_TEMPLATE))
			{
				account.setAllowedIP("");
				account.setAllowedHwid("");
				account.setPasswordHash(passwordHash);
				account.save();
			}
			else
			{
				client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
				return;
			}
		}

		boolean passwordCorrect = account.getPasswordHash().equals(passwordHash);

		if(!passwordCorrect)
		{
			// проверяем не зашифрован ли пароль одним из устаревших но поддерживаемых алгоритмов
			for(PasswordHash c : Config.LEGACY_CRYPT)
				if(c.compare(password, account.getPasswordHash()))
				{
					passwordCorrect = true;
					account.setPasswordHash(passwordHash);
					break;
				}
		}

		if(!IpBanManager.getInstance().tryLogin(client.getIpAddress(), passwordCorrect))
		{
			client.closeNow(false);
			return;
		}

		client.setPasswordCorrect(passwordCorrect);

		if(!Config.CHEAT_PASSWORD_CHECK)
		{
			if(!passwordCorrect)
			{
				client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
				return;
			}
		}

		if(account.getAccessLevel() < 0)
		{
			client.close(LoginFailReason.REASON_ACCESS_FAILED);
			return;
		}

		if(account.getBanExpire() > currentTime)
		{
			client.close(LoginFailReason.REASON_ACCESS_FAILED);
			return;
		}

		if(!account.isAllowedIP(client.getIpAddress()))
		{
			client.close(LoginFailReason.REASON_ATTEMPTED_RESTRICTED_IP);
			return;
		}

		for(GameServer gs : GameServerManager.getInstance().getGameServers())
			if(gs.getProtocol() >= 2 && gs.isAuthed())
				gs.sendPacket(new GetAccountInfo(user));

		account.setLastAccess(currentTime);
		account.setLastIP(client.getIpAddress());

		Log.LogAccount(account);
		
		Session session = SessionManager.getInstance().openSession(account);

		client.setAuthed(true);
		client.setLogin(user);
		client.setAccount(account);
		client.setSessionKey(session.getSessionKey());
		client.setState(LoginClientState.AUTHED);

		client.sendPacket(new LoginOk(client.getSessionKey()));
	}
}