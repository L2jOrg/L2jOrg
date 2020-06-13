/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.authserver.network.client;

import io.github.joealisson.mmocore.Client;
import io.github.joealisson.mmocore.Connection;
import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.data.database.Account;
import org.l2j.authserver.network.client.packet.AuthServerPacket;
import org.l2j.authserver.network.client.packet.auth2client.AccountKicked;
import org.l2j.authserver.network.client.packet.auth2client.Init;
import org.l2j.authserver.network.client.packet.auth2client.LoginFail;
import org.l2j.authserver.network.client.packet.auth2client.LoginFail.LoginFailReason;
import org.l2j.authserver.network.client.packet.auth2client.PlayFail;
import org.l2j.authserver.network.client.packet.auth2client.PlayFail.PlayFailReason;
import org.l2j.authserver.network.crypt.AuthCrypt;
import org.l2j.authserver.network.crypt.ScrambledKeyPair;
import org.l2j.commons.network.SessionKey;
import org.l2j.commons.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static org.l2j.authserver.network.client.AuthClientState.AUTHED_LOGIN;

/**
 * Represents a client connected into the Auth Server
 */
public final class AuthClient extends Client<Connection<AuthClient>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthClient.class);

    private final long _connectionStartTime;
    private final Map<Integer,Integer> charactersOnServer = new HashMap<>();

    private AuthCrypt _authCrypt;
    private ScrambledKeyPair _scrambledPair;
    private byte[] _blowfishKey;
    private int _sessionId;
    private SessionKey _sessionKey;

    private Account account;
    private AuthClientState state;
    private boolean isJoinedGameSever;
    private long requestedServersInfo;


    public AuthClient(Connection<AuthClient> con) {
		super(con);
		state = AuthClientState.CONNECTED;
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
            LOGGER.error(e.getMessage(), e);
            close();
            return false;
        }

        if (!decrypted) {
            LOGGER.warn("Wrong checksum from client: {}", toString());
            close();
        }
        return decrypted;
    }

    @Override
    public int encryptedSize(int dataSize) {
        return _authCrypt.encryptedSize(dataSize);
    }

    @Override
    public byte[] encrypt(byte[] data, int offset, int size) {
	    try {
	       return _authCrypt.encrypt(data, offset, size);
        } catch (IOException e) {
	        LOGGER.error(e.getMessage(), e);
	        return Util.BYTE_ARRAY_EMPTY;
        }
    }

	public void sendPacket(AuthServerPacket lsp) {
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
        LOGGER.debug("DISCONNECTED: {}", toString());

        if(!isJoinedGameSever && nonNull(account)) {
            AuthController.getInstance().removeAuthedClient(account.getLogin());
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

    public boolean isJoinedGameSever() {
        return isJoinedGameSever;
    }

    AuthClientState getState()
    {
        return state;
    }

    public void setState(AuthClientState state) {
        this.state = state;
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

    public long getConnectionStartTime() {
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

    public void setRequestedServerInfo(long count) {
        this.requestedServersInfo = count;
    }

    public long getRequestedServersInfo() {
        return requestedServersInfo;
    }

    @Override
    public String toString() {
        String address =  getHostAddress();
        if (getState() == AUTHED_LOGIN) {
            return "[" + getAccount().getLogin() + " (" + (address.equals("") ? "disconnect" : address) + ")]";
        }
        return "[" + (address.equals("") ? "disconnect" : address) + "]";
    }
}
