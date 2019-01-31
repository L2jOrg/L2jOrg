package org.l2j.authserver.controller;

import org.l2j.authserver.GameServerInfo;
import org.l2j.authserver.network.SessionKey;
import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.auth2client.LoginOk;
import org.l2j.authserver.network.crypt.AuthCrypt;
import org.l2j.authserver.network.crypt.ScrambledKeyPair;
import org.l2j.authserver.network.gameserver.packet.game2auth.ServerStatus;
import org.l2j.authserver.settings.AuthServerSettings;
import org.l2j.authserver.AccountRepository;
import org.l2j.authserver.Account;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
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
import static org.l2j.commons.database.DatabaseAccess.getRepository;
import static org.l2j.commons.util.Util.hash;
import static org.l2j.commons.util.Util.isNullOrEmpty;

public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final Logger loginLogger = LoggerFactory.getLogger("loginHistory");
    private static final int LOGIN_TIMEOUT = 60 * 1000;
    private static final Pattern USERNAME_PATTERN = Pattern.compile(usernameTemplate());
    private static final String ACCOUNT_LOGIN_FAILED = "Account Login Failed {} : {}";

    private static AuthController _instance;

    private final Set<AuthClient> connectedClients = new HashSet<>();
    private final Map<String, AuthClient> authedClients = new ConcurrentHashMap<>();
    private final Map<String, FailedLoginAttempt> bruteForceProtection = new HashMap<>();
    private final BanManager banManager;
    private final KeyGenerator blowfishKeysGenerator;
    private ScheduledFuture<?> scheduledPurge;

    private ScrambledKeyPair[] _keyPairs;

    private AuthController() throws GeneralSecurityException {
        logger.info("Loading Auth Controller...");
        banManager = BanManager.load();
        blowfishKeysGenerator = KeyGenerator.getInstance("Blowfish");
        initializeScrambledKeys();
    }

    public static void load() throws GeneralSecurityException {
        if (isNull(_instance)) {
            _instance = new AuthController();
        }
    }

    private void initializeScrambledKeys() throws GeneralSecurityException {
        var keygen = KeyPairGenerator.getInstance("RSA");
        var spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
        keygen.initialize(spec);

        _keyPairs = new ScrambledKeyPair[10];

        for (int i = 0; i < 10; i++) {
            _keyPairs[i] = new ScrambledKeyPair(keygen.generateKeyPair());
        }
        logger.info("Cached 10 KeyPairs for RSA communication");

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
            scheduledPurge = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PurgeThread(), LOGIN_TIMEOUT, 2 * LOGIN_TIMEOUT);
        }
    }

    private void assignSessionKeyToClient(AuthClient client) {
        var key = new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
        client.setSessionKey(key);
        authedClients.put(client.getAccount().getId(), client);
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
            return;
        }

        var accountOptional = getRepository(AccountRepository.class).findById(username);
        if(accountOptional.isPresent()) {
           verifyAccountInfo(client, accountOptional.get(), password);
        } else if(isAutoCreateAccount()) {
            createNewAccount(client, username, password);
        } else {
            client.close(REASON_ACCOUNT_INFO_INCORR);
        }
    }

    private void verifyAccountInfo(AuthClient client, Account account, String password) {
        try {
            if(hash(password).equals(account.getPassword())) {
                if(account.isBanned()) {
                    client.close(REASON_PERMANENTLY_BANNED);
                    loginLogger.info(ACCOUNT_LOGIN_FAILED, account.getId(), "Banned Account");
                } else if( verifyAccountInUse(account)) {
                    client.close(REASON_ACCOUNT_IN_USE);
                    loginLogger.info(ACCOUNT_LOGIN_FAILED, account.getId(), "Account Already In Use");
                } else {
                    processAuth(client, account);
                }
            } else {
                client.close(REASON_ACCOUNT_INFO_INCORR);
                addLoginFailed(account, password, client);
                loginLogger.info(ACCOUNT_LOGIN_FAILED, account.getId(), "Wrong Username or Password");
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getLocalizedMessage(), e);
            client.close(REASON_SYSTEM_ERROR);
        }
    }

    private boolean verifyAccountInUse(Account account) {
        var authedClient = authedClients.get(account.getId());
        if(nonNull(authedClient)) {
            authedClient.close(REASON_ACCOUNT_IN_USE);
            authedClients.remove(account.getId());
            return true;
        }

        var result = false;
        for (GameServerInfo gameServer : GameServerManager.getInstance().getRegisteredGameServers().values()) {
            if(nonNull(gameServer) && gameServer.accountIsConnected(account.getId())) {
                gameServer.sendKickPlayer(account.getId());
                result = true;
            }
        }
        return result;
    }

    private void processAuth(AuthClient client, Account account) {
        requestAccountInfo(client, account);
        updateClientInfo(client, account);
        authedClients.put(account.getId(), client);
        if(client.getRequestdServersInfo() == 0) {
            client.sendPacket(new LoginOk());
        }
        bruteForceProtection.remove(account.getId());
        getRepository(AccountRepository.class).save(account);
        loginLogger.info("Account Logged {}", account.getId());
    }

    private void requestAccountInfo(AuthClient client, Account account) {
        var gameservers = GameServerManager.getInstance().getRegisteredGameServers().values().stream().filter(GameServerInfo::isAuthed).collect(Collectors.toList());
        client.setRequestedServerInfo(gameservers.size());
        gameservers.forEach(gameServer -> gameServer.requestAccountInfo(account.getId()));
    }

    private void updateClientInfo(AuthClient client, Account account) {
        client.setAccount(account);
        assignSessionKeyToClient(client);
        client.setState(AUTHED_LOGIN);
        account.setLastAccess(currentTimeMillis());
        account.setLastIP(client.getHostAddress());
    }

    private void createNewAccount(AuthClient client, String username, String password) {
        try {
            var account = new Account(username, hash(password), currentTimeMillis(), client.getHostAddress());
            getRepository(AccountRepository.class).save(account);
            processAuth(client, account);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public void addAccountCharactersInfo(int serverId, String account, int players) {
        AuthClient client = authedClients.get(account);
        if (nonNull(client)) {
            client.addCharactersOnServer(serverId, players);
            if(client.getCharactersOnServer().size() == client.getRequestdServersInfo()) {
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
                if(getRepository(AccountRepository.class).updateLastServer(client.getAccount().getId(), serverId) < 1) {
                    logger.warn("Could not set lastServer of account {} ", client.getAccount().getId());
                }
            }
            return loginOk;
        }
        return false;
    }

    public void setAccountAccessLevel(String login, short acessLevel) {
        if(getRepository(AccountRepository.class).updateAccessLevel(login, acessLevel) < 1) {
            logger.warn("Could not set accessLevel of account {}", login);
        }
    }

    public void removeClient(AuthClient client) {
        if(nonNull(client)) {
            connectedClients.remove(client);
        }
    }

    private void addLoginFailed(Account account, String password, AuthClient client) {
        FailedLoginAttempt failedAttempt = bruteForceProtection.get(account.getId());
        if(nonNull(failedAttempt)) {
            failedAttempt.increaseCounter(password);
        } else {
            failedAttempt = new FailedLoginAttempt(password);
            bruteForceProtection.put(account.getId(), failedAttempt);
        }

        if(failedAttempt.getCount() >= authTriesBeforeBan())  {
            logger.info("Banning {} for seconds due to {} invalid user/pass attempts", client.getHostAddress(), loginBlockAfterBan(), failedAttempt.getCount());
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
        return _instance;
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
            Set<AuthClient> toRemove =  new HashSet<>();
            synchronized (connectedClients) {
                connectedClients.forEach(client -> {
                    if (isNull(client) || client.getConnectionStartTime() + LOGIN_TIMEOUT >= currentTimeMillis() || !client.isConnected()) {
                        toRemove.add(client);
                    }
                });
                connectedClients.removeAll(toRemove);
                if(connectedClients.isEmpty()) {
                    scheduledPurge.cancel(false);
                }
            }

            toRemove.stream().filter(Objects::nonNull).forEach(authClient -> authClient.close(REASON_ACCESS_FAILED_TRYA1));
        }
    }
}
