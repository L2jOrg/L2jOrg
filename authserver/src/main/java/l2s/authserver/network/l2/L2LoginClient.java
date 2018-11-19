/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package l2s.authserver.network.l2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPrivateKey;

import l2s.authserver.Config;
import l2s.authserver.accounts.Account;
import l2s.authserver.crypt.LoginCrypt;
import l2s.authserver.crypt.ScrambledKeyPair;
import l2s.authserver.network.l2.s2c.AccountKicked;
import l2s.authserver.network.l2.s2c.AccountKicked.AccountKickedReason;
import l2s.authserver.network.l2.s2c.L2LoginServerPacket;
import l2s.authserver.network.l2.s2c.LoginFail;
import l2s.authserver.network.l2.s2c.LoginFail.LoginFailReason;
import l2s.commons.net.nio.impl.MMOClient;
import l2s.commons.net.nio.impl.MMOConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class L2LoginClient extends MMOClient<MMOConnection<L2LoginClient>>
{
	private final static Logger _log = LoggerFactory.getLogger(L2LoginClient.class);

	public static enum LoginClientState
	{
		CONNECTED,
		AUTHED_GG,
		AUTHED,
		DISCONNECTED
	}

	private static final int PROTOCOL_VERSION = 0xc621;

	private LoginClientState _state;

	private LoginCrypt _loginCrypt;
	private ScrambledKeyPair _scrambledPair;
	private byte[] _blowfishKey;

	private String _login;
	private SessionKey _skey;
	private Account _account;
	private String _ipAddr;

	private int _sessionId;

	private boolean _passwordCorrect;

	public L2LoginClient(MMOConnection<L2LoginClient> con)
	{
		super(con);
		_state = LoginClientState.CONNECTED;
		_scrambledPair = Config.getScrambledRSAKeyPair();
		_blowfishKey = Config.getBlowfishKey();
		_loginCrypt = new LoginCrypt();
		_loginCrypt.setKey(_blowfishKey);
		_sessionId = con.hashCode();
		_ipAddr = getConnection().getSocket().getInetAddress().getHostAddress();
		_passwordCorrect = false;
	}

	@Override
	public boolean decrypt(ByteBuffer buf, int size)
	{
		boolean ret;
		try
		{
			ret = _loginCrypt.decrypt(buf.array(), buf.position(), size);
		}
		catch(IOException e)
		{
			_log.error("", e);
			closeNow(true);
			return false;
		}

		if(!ret)
			closeNow(true);

		return ret;
	}

	@Override
	public boolean encrypt(ByteBuffer buf, int size)
	{
		final int offset = buf.position();
		try
		{
			size = _loginCrypt.encrypt(buf.array(), offset, size);
		}
		catch(IOException e)
		{
			_log.error("", e);
			return false;
		}

		buf.position(offset + size);
		return true;
	}

	public LoginClientState getState()
	{
		return _state;
	}

	public void setState(LoginClientState state)
	{
		_state = state;
	}

	public byte[] getBlowfishKey()
	{
		return _blowfishKey;
	}

	public byte[] getScrambledModulus()
	{
		return _scrambledPair.getScrambledModulus();
	}

	public RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) _scrambledPair.getKeyPair().getPrivate();
	}

	public String getLogin()
	{
		return _login;
	}

	public void setLogin(String login)
	{
		_login = login;
	}

	public Account getAccount()
	{
		return _account;
	}

	public void setAccount(Account account)
	{
		_account = account;
	}

	public SessionKey getSessionKey()
	{
		return _skey;
	}

	public void setSessionKey(SessionKey skey)
	{
		_skey = skey;
	}

	public void setSessionId(int val)
	{
		_sessionId = val;
	}

	public int getSessionId()
	{
		return _sessionId;
	}

	public void setPasswordCorrect(boolean val)
	{
		_passwordCorrect = val;
	}

	public boolean isPasswordCorrect()
	{
		return _passwordCorrect;
	}

	public void sendPacket(L2LoginServerPacket lsp)
	{
		if(isConnected())
			getConnection().sendPacket(lsp);
	}

	public void close(LoginFailReason reason)
	{
		if(isConnected())
			getConnection().close(new LoginFail(reason));
	}

	public void close(AccountKickedReason reason)
	{
		if(isConnected())
			getConnection().close(new AccountKicked(reason));
	}

	public void close(L2LoginServerPacket lsp)
	{
		if(isConnected())
			getConnection().close(lsp);
	}

	@Override
	public void onDisconnection()
	{
		_state = LoginClientState.DISCONNECTED;
		_skey = null;
		_loginCrypt = null;
		_scrambledPair = null;
		_blowfishKey = null;
	}

	@Override
	public String toString()
	{
		switch(_state)
		{
			case AUTHED:
				return "[ Account : " + getLogin() + " IP: " + getIpAddress() + "]";
			default:
				return "[ State : " + getState() + " IP: " + getIpAddress() + "]";
		}
	}

	public String getIpAddress()
	{
		return _ipAddr;
	}

	@Override
	protected void onForcedDisconnection()
	{

	}

	public int getProtocol()
	{
		return PROTOCOL_VERSION;
	}
}