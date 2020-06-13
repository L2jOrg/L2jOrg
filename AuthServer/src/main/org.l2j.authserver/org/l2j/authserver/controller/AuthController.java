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
package org.l2j.authserver.controller;

import org.l2j.authserver.data.database.Account;
import org.l2j.authserver.data.database.dao.AccountDAO;
import org.l2j.authserver.network.GameServerInfo;
import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.auth2client.LoginOk;
import org.l2j.authserver.network.crypt.AuthCrypt;
import org.l2j.authserver.network.crypt.ScrambledKeyPair;
import org.l2j.authserver.network.gameserver.packet.game2auth.ServerStatus;
import org.l2j.authserver.settings.AuthServerSettings;
import org.l2j.commons.network.SessionKey;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.authserver.network.client.AuthClientState.AUTHED_LOGIN;
import static org.l2j.authserver.network.client.packet.auth2client.AccountKicked.AccountKickedReason.REASON_PERMANENTLY_BANNED;
import static org.l2j.authserver.network.client.packet.auth2client.LoginFail.LoginFailReason.*;
import static org.l2j.authserver.settings.AuthServerSettings.*;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.hash;
import static org.l2j.commons.util.Util.isNullOrEmpty;

public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private static final Logger LOGIN_HISTORY = LoggerFactory.getLogger("loginHistory");
    private static final int LOGIN_TIMEOUT = 3;
    private static final Pattern USERNAME_PATTERN = Pattern.compile(usernameTemplate());
    private static final String ACCOUNT_LOGIN_FAILED = "Account Login Failed {} : {}";

    private final Map<String, AuthClient> authedClients = new ConcurrentHashMap<>();
    private final Map<String, FailedLoginAttempt> bruteForceProtection = new HashMap<>();
    private final BanManager banManager;

    private KeyGenerator blowfishKeysGenerator;
    private ScheduledFuture<?> scheduledPurge;

    private ScrambledKeyPair[] _keyPairs;

    private AuthController() {
        banManager = BanManager.getInstance();
        try {
            load();
        } catch (GeneralSecurityException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void load() throws GeneralSecurityException {
        LOGGER.info("Loading Auth Controller...");
        blowfishKeysGenerator = KeyGenerator.getInstance("Blowfish");
        initializeScrambledKeys();
    }

    private void initializeScrambledKeys() throws GeneralSecurityException {
        var keygen = KeyPairGenerator.getInstance("RSA");
        var spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
        keygen.initialize(spec);

        _keyPairs = new ScrambledKeyPair[10];

        for (int i = 0; i < 10; i++) {
            _keyPairs[i] = new ScrambledKeyPair(keygen.generateKeyPair());
        }
        LOGGER.info("Cached 10 KeyPairs for RSA communication");

        testCipher((RSAPrivateKey) _keyPairs[0].getPair().getPrivate());
    }

    private void testCipher(RSAPrivateKey key) throws GeneralSecurityException {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
        rsaCipher.init(Cipher.DECRYPT_MODE, key);
    }

    public void registerClient(AuthClient client) {
        client.setKeyPar(getScrambledRSAKeyPair());
        var blowfishKey  = getBlowfishKey();
        client.setBlowfishKey(blowfishKey);
        client.setSessionId(Rnd.nextInt());
        var cripter = new AuthCrypt();
        cripter.setKey(blowfishKey);
        client.setCrypt(cripter);

        if(isNull(scheduledPurge) || scheduledPurge.isCancelled()) {
            scheduledPurge = ThreadPool.scheduleAtFixedDelay(new PurgeThread(), LOGIN_TIMEOUT, LOGIN_TIMEOUT, TimeUnit.MINUTES);
        }
    }

    private void assignSessionKeyToClient(AuthClient client) {
        var key = new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
        client.setSessionKey(key);
        authedClients.put(client.getAccount().getLogin(), client);
    }

    public void removeAuthedClient(String account) {
        if(isNullOrEmpty(account)) {
            return;
        }
        authedClients.remove(account);
    }

    public void authenticate(AuthClient client, String username, String password) {
        if(!isValidUserName(username)) {
            client.close(REASON_ACCOUNT_INFO_INCORR);
            LOGIN_HISTORY.debug("Invalid Username");
            return;
        }

        var account = getDAO(AccountDAO.class).findById(username);
        if(nonNull(account)) {
           verifyAccountInfo(client, account, password);
        } else if(isAutoCreateAccount()) {
            createNewAccount(client, username, password);
        } else {
            LOGIN_HISTORY.debug("Username no found");
            client.close(REASON_ACCOUNT_INFO_INCORR);
        }
    }

    private void verifyAccountInfo(AuthClient client, Account account, String password) {
        try {
            if(hash(password).equals(account.getPassword())) {
                if(account.isBanned()) {
                    client.close(REASON_PERMANENTLY_BANNED);
                    LOGIN_HISTORY.info(ACCOUNT_LOGIN_FAILED, account.getLogin(), "Banned Account");
                } else if( verifyAccountInUse(account)) {
                    client.close(REASON_ACCOUNT_IN_USE);
                    LOGIN_HISTORY.info(ACCOUNT_LOGIN_FAILED, account.getLogin(), "Account Already In Use");
                } else {
                    processAuth(client, account);
                }
            } else {
                client.close(REASON_ACCOUNT_INFO_INCORR);
                addLoginFailed(account, password, client);
                LOGIN_HISTORY.info(ACCOUNT_LOGIN_FAILED, account.getLogin(), "Wrong Username or Password");
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
            client.close(REASON_SYSTEM_ERROR);
        }
    }

    private boolean verifyAccountInUse(Account account) {
        var result = false;
        var authedClient = authedClients.get(account.getLogin());
        if(nonNull(authedClient)) {
            authedClient.close(REASON_ACCOUNT_IN_USE);
            removeAuthedClient(account.getLogin());
            result = true;
        }

        for (GameServerInfo gameServer : GameServerManager.getInstance().getRegisteredGameServers().values()) {
            if(nonNull(gameServer) && gameServer.accountIsConnected(account.getLogin())) {
                gameServer.sendKickPlayer(account.getLogin());
                result = true;
            }
        }
        return result;
    }

    private void processAuth(AuthClient client, Account account) {
        updateClientInfo(client, account);
        requestAccountInfo(client, account);
        if(client.getRequestedServersInfo() == 0) {
            client.sendPacket(new LoginOk());
        }
        bruteForceProtection.remove(account.getLogin());
        LOGIN_HISTORY.info("Account Logged {}", account.getLogin());
    }

    private void requestAccountInfo(AuthClient client, Account account) {
        var gameservers = GameServerManager.getInstance().getRegisteredGameServers().values().stream().filter(GameServerInfo::isAuthed).collect(Collectors.toList());
        client.setRequestedServerInfo(gameservers.size());
        gameservers.forEach(gameServer -> gameServer.requestAccountInfo(account.getLogin()));
    }

    private void updateClientInfo(AuthClient client, Account account) {
        client.setAccount(account);
        assignSessionKeyToClient(client);
        client.setState(AUTHED_LOGIN);
        account.setLastAccess(currentTimeMillis());
        account.setLastIP(client.getHostAddress());
        getDAO(AccountDAO.class).updateAccess(account.getLogin(), account.getLastAccess(), client.getHostAddress());
    }

    private void createNewAccount(AuthClient client, String username, String password) {
        try {
            getDAO(AccountDAO.class).save(username, hash(password), currentTimeMillis(), client.getHostAddress());
            var account = new Account(username, hash(password), currentTimeMillis(), client.getHostAddress());
            processAuth(client, account);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void addAccountCharactersInfo(int serverId, String account, int players) {
        AuthClient client = authedClients.get(account);
        if (nonNull(client)) {
            client.addCharactersOnServer(serverId, players);
            if(client.getCharactersOnServer().size() == client.getRequestedServersInfo()) {
                client.sendPacket(new LoginOk());
            }
        }
    }

    public SessionKey getKeyForAccount(String account) {
        AuthClient client = authedClients.get(account);
        if (nonNull(client)) {
            return client.getSessionKey();
        }
        return null;
    }

    public boolean isLoginPossible(AuthClient client, int serverId) {
        GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServerById(serverId);
        int access = client.getAccessLevel();
        if (nonNull(gsi) && gsi.isAuthed()) {
            boolean loginOk = ((gsi.getOnlinePlayersCount() < gsi.getMaxPlayers()) && (gsi.getStatus() != ServerStatus.STATUS_GM_ONLY)) || (access >= getSettings(AuthServerSettings.class).gmMinimumLevel());

            if (loginOk && (client.getLastServer() != serverId)) {
                if(getDAO(AccountDAO.class).updateLastServer(client.getAccount().getLogin(), serverId) < 1) {
                    LOGGER.warn("Could not set lastServer of account {} ", client.getAccount().getLogin());
                }
            }
            return loginOk;
        }
        return false;
    }

    public void setAccountAccessLevel(String login, short accessLevel) {
        if(getDAO(AccountDAO.class).updateAccessLevel(login, accessLevel) < 1) {
            LOGGER.warn("Could not set accessLevel of account {}", login);
        }
    }

    private void addLoginFailed(Account account, String password, AuthClient client) {
        FailedLoginAttempt failedAttempt = bruteForceProtection.get(account.getLogin());
        if(nonNull(failedAttempt)) {
            failedAttempt.increaseCounter(password);
        } else {
            failedAttempt = new FailedLoginAttempt(password);
            bruteForceProtection.put(account.getLogin(), failedAttempt);
        }

        if(failedAttempt.getCount() >= authTriesBeforeBan())  {
            LOGGER.info("Banning {} for {} seconds due to {} invalid user/pass attempts", client.getHostAddress(), loginBlockAfterBan(), failedAttempt.getCount());
            banManager.addBannedAdress(client.getHostAddress(), currentTimeMillis() + loginBlockAfterBan() * 1000);
        }
    }

    private ScrambledKeyPair getScrambledRSAKeyPair() {
        return _keyPairs[Rnd.get(10)];
    }

    private byte[] getBlowfishKey() {
        return blowfishKeysGenerator.generateKey().getEncoded();
    }

    private boolean isValidUserName(String username) {
        return USERNAME_PATTERN.matcher(username).matches();
    }

    public boolean isBannedAddress(String address) {
        return banManager.isBanned(address);
    }

    public static AuthController getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AuthController INSTANCE = new AuthController();
    }

    private class FailedLoginAttempt {

        private int _count = 1;
        private long _lastAttempTime;
        private String _lastPassword;

        FailedLoginAttempt(String lastPassword) {
            _lastAttempTime = currentTimeMillis();
            _lastPassword = lastPassword;
        }

        void increaseCounter(String password) {
            if (!_lastPassword.equals(password)) {
                // check if theres a long time since last wrong try
                if ((currentTimeMillis() - _lastAttempTime) < (300 * 1000)) {
                    _count++;
                } else {
                    _count = 1;

                }
                _lastPassword = password;
                _lastAttempTime = currentTimeMillis();
            } else {
                // trying the same password is not brute force
                _lastAttempTime = currentTimeMillis();
            }
        }

        int getCount() {
            return _count;
        }
    }

    private class PurgeThread implements Runnable{
        @Override
        public void run() {
            synchronized (authedClients) {
                var entries = authedClients.entrySet().iterator();
                while (entries.hasNext()) {
                    var entry = entries.next();
                    var client = entry.getValue();

                    if(!client.isJoinedGameSever() && client.getConnectionStartTime() + TimeUnit.MINUTES.toMillis(LOGIN_TIMEOUT) <= currentTimeMillis() || !client.isConnected()) {
                        client.close(REASON_ACCESS_FAILED_TRYA1);
                        entries.remove();
                    }
                }

                if(authedClients.isEmpty()) {
                    scheduledPurge.cancel(false);
                }
            }
        }
    }
}
