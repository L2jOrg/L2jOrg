package org.l2j.authserver.network.client;

import org.l2j.authserver.network.SessionKey;
import org.l2j.commons.database.model.Account;
import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.network.crypt.AuthCrypt;
import org.l2j.authserver.network.crypt.ScrambledKeyPair;
import org.l2j.authserver.network.client.packet.L2LoginServerPacket;
import org.l2j.authserver.network.client.packet.auth2client.AccountKicked;
import org.l2j.authserver.network.client.packet.auth2client.Init;
import org.l2j.authserver.network.client.packet.auth2client.LoginFail;
import org.l2j.authserver.network.client.packet.auth2client.LoginFail.LoginFailReason;
import org.l2j.authserver.network.client.packet.auth2client.PlayFail;
import org.l2j.authserver.network.client.packet.auth2client.PlayFail.PlayFailReason;
import org.l2j.mmocore.Client;
import org.l2j.mmocore.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static org.l2j.authserver.network.client.AuthClientState.AUTHED_LOGIN;

/**
 * Represents a client connected into the LoginServer
 */
public final class AuthClient extends Client<Connection<AuthClient>> {

    private static Logger _log = LoggerFactory.getLogger(AuthClient.class);

    private final long _connectionStartTime;
    private final Map<Integer,Integer> charactersOnServer = new HashMap<>();

    private AuthCrypt _authCrypt;
    private ScrambledKeyPair _scrambledPair;
    private byte[] _blowfishKey;
    private int _sessionId;
    private SessionKey _sessionKey;

    private Account account;
    private boolean _usesInternalIP;
    private AuthClientState _state;
    private boolean isJoinedGameSever;
    private long requestdServersInfo;


    public AuthClient(Connection<AuthClient> con) {
		super(con);
		_state = AuthClientState.CONNECTED;
		String ip = getHostAddress();

		// TODO unhardcode this
		if (ip.startsWith("192.168") || ip.startsWith("10.0") || ip.startsWith("127.0.0.1")) {
			_usesInternalIP = true;
		}

		_connectionStartTime = System.currentTimeMillis();
		AuthController.getInstance().registerClient(this);
	}

    @Override
    public boolean decrypt(byte[] data, int offset, int size) {
        boolean decrypted;
        try  {
            decrypted = _authCrypt.decrypt(data, offset, size);
        }
        catch (IOException e) {
            _log.error(e.getLocalizedMessage(), e);
            disconnect();
            return false;
        }

        if (!decrypted) {
            _log.warn("Wrong checksum from client: {}", toString());
            disconnect();
        }
        return decrypted;
    }

    @Override
    public int encrypt(byte[] data, int offset, int size) {
        int encryptedSize = -1;
	    try {
	       encryptedSize = _authCrypt.encrypt(data, offset, size);
        } catch (IOException e) {
	        _log.error(e.getLocalizedMessage(), e);
	        return encryptedSize;
        }
        return encryptedSize;
    }

	public void sendPacket(L2LoginServerPacket lsp) {
	    writePacket(lsp);
	}

    public void close(LoginFailReason reason) {
        close(new LoginFail(reason));
	}
	
	public void close(PlayFailReason reason) {
		close(new PlayFail(reason));
	}

	public void close(AccountKicked.AccountKickedReason reason) {
        close(new AccountKicked(reason));
	}

    @Override
    public void onConnected() {
        sendPacket(new Init());
    }

	@Override
	protected void onDisconnection() {
        _log.info("DISCONNECTED: {}", toString());

        AuthController.getInstance().removeClient(this);

        if(!isJoinedGameSever && nonNull(account)) {
            AuthController.getInstance().removeAuthedClient(account.getId());
        }
	}

    public void addCharactersOnServer(int serverId, int players) {
        charactersOnServer.put(serverId, players);
    }

    public Map<Integer, Integer> getCharactersOnServer() {
        return charactersOnServer;
    }

    public int getPlayersOnServer(int serverId) {
        return charactersOnServer.getOrDefault(serverId, 0);
    }

    public void joinGameserver() {
        isJoinedGameSever = true;
    }

    AuthClientState getState()
    {
        return _state;
    }

    public void setState(AuthClientState state) {
        _state = state;
    }

    public byte[] getBlowfishKey() {
        return _blowfishKey;
    }

    public byte[] getScrambledModulus() {
        return _scrambledPair.getScrambledModulus();
    }

    public PrivateKey getRSAPrivateKey() {
        return _scrambledPair.getPair().getPrivate();
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getAccessLevel() {
        return nonNull(account) ? account.getAccessLevel() : -1;
    }

    public int getLastServer() {
        return nonNull(account) ? account.getLastServer(): -1;
    }

    public int getSessionId() {
        return _sessionId;
    }

    public void setSessionKey(SessionKey sessionKey)
    {
        _sessionKey = sessionKey;
    }

    public SessionKey getSessionKey() {
        return _sessionKey;
    }

    public long getConnectionStartTime()
    {
        return _connectionStartTime;
    }

    public void setKeyPar(ScrambledKeyPair keyPair) {
        _scrambledPair = keyPair;
    }

    public void setBlowfishKey(byte[] blowfishKey) {
        _blowfishKey = blowfishKey;
    }

    public void setSessionId(int sessionId) {
        _sessionId = sessionId;
    }

    public void setCrypt(AuthCrypt crypt) {
        _authCrypt =  crypt;
    }

    public boolean usesInternalIP()
    {
        return _usesInternalIP;
    }

    public void setRequestedServerInfo(long count) {
        this.requestdServersInfo = count;
    }

    public long getRequestdServersInfo() {
        return requestdServersInfo;
    }

    @Override
    public String toString() {
        String address =  getHostAddress();
        if (getState() == AUTHED_LOGIN) {
            return "[" + getAccount().getId() + " (" + (address.equals("") ? "disconnect" : address) + ")]";
        }
        return "[" + (address.equals("") ? "disconnect" : address) + "]";
    }
}
