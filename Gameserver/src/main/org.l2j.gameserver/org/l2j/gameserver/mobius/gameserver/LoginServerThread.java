package org.l2j.gameserver.mobius.gameserver;

import org.l2j.commons.crypt.NewCrypt;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.ConnectionState;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.loginserverpackets.game.*;
import org.l2j.gameserver.mobius.gameserver.network.loginserverpackets.login.*;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.CharSelectionInfo;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.LoginFail;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LoginServerThread extends Thread {
    protected static final Logger LOGGER = Logger.getLogger(LoginServerThread.class.getName());
    protected static final Logger ACCOUNTING_LOGGER = Logger.getLogger("accounting");

    private static final int REVISION = 0x0106;
    private final String _hostname;
    private final int _port;
    private final int _gamePort;
    private final boolean _acceptAlternate;
    private final boolean _reserveHost;
    private final Set<WaitingClient> _waitingClients = ConcurrentHashMap.newKeySet();
    private final Map<String, L2GameClient> _accountsInGameServer = new ConcurrentHashMap<>();
    private final List<String> _subnets;
    private final List<String> _hosts;
    private Socket _loginSocket;
    private OutputStream _out;
    /**
     * The BlowFish engine used to encrypt packets<br>
     * It is first initialized with a unified key:<br>
     * "_;v.]05-31!|+-%xT!^[$\00"<br>
     * <br>
     * and then after handshake, with a new key sent by<br>
     * login server during the handshake. This new key is stored<br>
     * in blowfishKey
     */
    private NewCrypt _blowfish;
    private byte[] _hexID;
    private int _requestID;
    private int _maxPlayer;
    private int _status;
    private String _serverName;

    /**
     * Instantiates a new login server thread.
     */
    protected LoginServerThread() {
        super("LoginServerThread");
        _port = Config.GAME_SERVER_LOGIN_PORT;
        _gamePort = Config.PORT_GAME;
        _hostname = Config.GAME_SERVER_LOGIN_HOST;
        _hexID = Config.HEX_ID;
        if (_hexID == null) {
            _requestID = Config.REQUEST_ID;
            _hexID = CommonUtil.generateHex(16);
        } else {
            _requestID = Config.SERVER_ID;
        }
        _acceptAlternate = Config.ACCEPT_ALTERNATE_ID;
        _reserveHost = Config.RESERVE_HOST_ON_LOGIN;
        _subnets = Config.GAME_SERVER_SUBNETS;
        _hosts = Config.GAME_SERVER_HOSTS;
        _maxPlayer = Config.MAXIMUM_ONLINE_USERS;
    }

    /**
     * Gets the single instance of LoginServerThread.
     *
     * @return single instance of LoginServerThread
     */
    public static LoginServerThread getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            int lengthHi = 0;
            int lengthLo = 0;
            int length = 0;
            boolean checksumOk = false;
            try {
                // Connection
                LOGGER.info(getClass().getSimpleName() + ": Connecting to login on " + _hostname + ":" + _port);
                _loginSocket = new Socket(_hostname, _port);
                final InputStream in = _loginSocket.getInputStream();
                _out = new BufferedOutputStream(_loginSocket.getOutputStream());

                // init Blowfish
                final byte[] blowfishKey = CommonUtil.generateHex(40);
                _blowfish = new NewCrypt("_;v.]05-31!|+-%xT!^[$\00");
                while (!isInterrupted()) {
                    lengthLo = in.read();
                    lengthHi = in.read();
                    length = (lengthHi * 256) + lengthLo;

                    if (lengthHi < 0) {
                        LOGGER.finer(getClass().getSimpleName() + ": Login terminated the connection.");
                        break;
                    }

                    final byte[] incoming = new byte[length - 2];

                    int receivedBytes = 0;
                    int newBytes = 0;
                    int left = length - 2;
                    while ((newBytes != -1) && (receivedBytes < (length - 2))) {
                        newBytes = in.read(incoming, receivedBytes, left);
                        receivedBytes += newBytes;
                        left -= newBytes;
                    }

                    if (receivedBytes != (length - 2)) {
                        LOGGER.warning(getClass().getSimpleName() + ": Incomplete Packet is sent to the server, closing connection.(LS)");
                        break;
                    }

                    // decrypt if we have a key
                    _blowfish.decrypt(incoming, 0, incoming.length);
                    checksumOk = NewCrypt.verifyChecksum(incoming);

                    if (!checksumOk) {
                        LOGGER.warning(getClass().getSimpleName() + ": Incorrect packet checksum, ignoring packet (LS)");
                        break;
                    }

                    final int packetType = incoming[0] & 0xff;
                    switch (packetType) {
                        case 0x00: {
                            final InitLS init = new InitLS(incoming);
                            if (init.getRevision() != REVISION) {
                                // TODO: revision mismatch
                                LOGGER.warning("/!\\ Revision mismatch between LS and GS /!\\");
                                break;
                            }

                            RSAPublicKey publicKey;

                            try {
                                final KeyFactory kfac = KeyFactory.getInstance("RSA");
                                final BigInteger modulus = new BigInteger(init.getRSAKey());
                                final RSAPublicKeySpec kspec1 = new RSAPublicKeySpec(modulus, RSAKeyGenParameterSpec.F4);
                                publicKey = (RSAPublicKey) kfac.generatePublic(kspec1);
                            } catch (GeneralSecurityException e) {
                                LOGGER.warning(getClass().getSimpleName() + ": Trouble while init the public key send by login");
                                break;
                            }
                            // send the blowfish key through the rsa encryption
                            sendPacket(new BlowFishKey(blowfishKey, publicKey));
                            // now, only accept packet with the new encryption
                            _blowfish = new NewCrypt(blowfishKey);
                            sendPacket(new AuthRequest(_requestID, _acceptAlternate, _hexID, _gamePort, _reserveHost, _maxPlayer, _subnets, _hosts));
                            break;
                        }
                        case 0x01: {
                            final LoginServerFail lsf = new LoginServerFail(incoming);
                            LOGGER.info(getClass().getSimpleName() + ": Damn! Registeration Failed: " + lsf.getReasonString());
                            // login will close the connection here
                            break;
                        }
                        case 0x02: {
                            final AuthResponse aresp = new AuthResponse(incoming);
                            final int serverID = aresp.getServerId();
                            _serverName = aresp.getServerName();
                            Config.saveHexid(serverID, hexToString(_hexID));
                            LOGGER.info(getClass().getSimpleName() + ": Registered on login as Server " + serverID + ": " + _serverName);
                            final ServerStatus st = new ServerStatus();
                            if (Config.SERVER_LIST_BRACKET) {
                                st.addAttribute(ServerStatus.SERVER_LIST_SQUARE_BRACKET, ServerStatus.ON);
                            } else {
                                st.addAttribute(ServerStatus.SERVER_LIST_SQUARE_BRACKET, ServerStatus.OFF);
                            }
                            st.addAttribute(ServerStatus.SERVER_TYPE, Config.SERVER_LIST_TYPE);
                            if (Config.SERVER_GMONLY) {
                                st.addAttribute(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_GM_ONLY);
                            } else {
                                st.addAttribute(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_AUTO);
                            }
                            if (Config.SERVER_LIST_AGE == 15) {
                                st.addAttribute(ServerStatus.SERVER_AGE, ServerStatus.SERVER_AGE_15);
                            } else if (Config.SERVER_LIST_AGE == 18) {
                                st.addAttribute(ServerStatus.SERVER_AGE, ServerStatus.SERVER_AGE_18);
                            } else {
                                st.addAttribute(ServerStatus.SERVER_AGE, ServerStatus.SERVER_AGE_ALL);
                            }
                            sendPacket(st);
                            final List<String> playerList = L2World.getInstance().getPlayers().stream().filter(player -> !player.isInOfflineMode()).map(L2PcInstance::getAccountName).collect(Collectors.toList());
                            if (!playerList.isEmpty()) {
                                sendPacket(new PlayerInGame(playerList));
                            }
                            break;
                        }
                        case 0x03: {
                            final PlayerAuthResponse par = new PlayerAuthResponse(incoming);
                            final String account = par.getAccount();
                            WaitingClient wcToRemove = null;
                            synchronized (_waitingClients) {
                                for (WaitingClient wc : _waitingClients) {
                                    if (wc.account.equals(account)) {
                                        wcToRemove = wc;
                                    }
                                }
                            }
                            if (wcToRemove != null) {
                                if (par.isAuthed()) {
                                    final PlayerInGame pig = new PlayerInGame(par.getAccount());
                                    sendPacket(pig);
                                    wcToRemove.gameClient.setConnectionState(ConnectionState.AUTHENTICATED);
                                    wcToRemove.gameClient.setSessionId(wcToRemove.session);
                                    wcToRemove.gameClient.sendPacket(LoginFail.LOGIN_SUCCESS);
                                    final CharSelectionInfo cl = new CharSelectionInfo(wcToRemove.account, wcToRemove.gameClient.getSessionId().playOkID1);
                                    wcToRemove.gameClient.sendPacket(cl);
                                    wcToRemove.gameClient.setCharSelection(cl.getCharInfo());
                                } else {
                                    LOGGER.warning(getClass().getSimpleName() + ": Session key is not correct. Closing connection for account " + wcToRemove.account);
                                    // wcToRemove.gameClient.getConnection().sendPacket(new LoginFail(LoginFail.SYSTEM_ERROR_LOGIN_LATER));
                                    wcToRemove.gameClient.close(new LoginFail(LoginFail.SYSTEM_ERROR_LOGIN_LATER));
                                    _accountsInGameServer.remove(wcToRemove.account);
                                }
                                _waitingClients.remove(wcToRemove);
                            }
                            break;
                        }
                        case 0x04: {
                            final KickPlayer kp = new KickPlayer(incoming);
                            doKickPlayer(kp.getAccount());
                            break;
                        }
                        case 0x05: {
                            final RequestCharacters rc = new RequestCharacters(incoming);
                            getCharsOnServer(rc.getAccount());
                            break;
                        }
                        case 0x06: {
                            new ChangePasswordResponse(incoming);
                            break;
                        }
                    }
                }
            } catch (UnknownHostException e) {
                LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": ", e);
            } catch (SocketException e) {
                LOGGER.warning(getClass().getSimpleName() + ": LoginServer not avaible, trying to reconnect...");
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Disconnected from Login, Trying to reconnect: ", e);
            } finally {
                try {
                    _loginSocket.close();
                    if (isInterrupted()) {
                        return;
                    }
                } catch (Exception e) {
                }
            }

            try {
                Thread.sleep(5000); // 5 seconds tempo.
            } catch (InterruptedException e) {
                return; // never swallow an interrupt!
            }
        }
    }

    /**
     * Adds the waiting client and send request.
     *
     * @param acc    the account
     * @param client the game client
     * @param key    the session key
     */
    public void addWaitingClientAndSendRequest(String acc, L2GameClient client, SessionKey key) {
        final WaitingClient wc = new WaitingClient(acc, client, key);
        synchronized (_waitingClients) {
            _waitingClients.add(wc);
        }
        final PlayerAuthRequest par = new PlayerAuthRequest(acc, key);
        try {
            sendPacket(par);
        } catch (IOException e) {
            LOGGER.warning(getClass().getSimpleName() + ": Error while sending player auth request");
        }
    }

    /**
     * Removes the waiting client.
     *
     * @param client the client
     */
    public void removeWaitingClient(L2GameClient client) {
        WaitingClient toRemove = null;
        synchronized (_waitingClients) {
            for (WaitingClient c : _waitingClients) {
                if (c.gameClient == client) {
                    toRemove = c;
                }
            }
            if (toRemove != null) {
                _waitingClients.remove(toRemove);
            }
        }
    }

    /**
     * Send logout for the given account.
     *
     * @param account the account
     */
    public void sendLogout(String account) {
        if (account == null) {
            return;
        }
        final PlayerLogout pl = new PlayerLogout(account);
        try {
            sendPacket(pl);
        } catch (IOException e) {
            LOGGER.warning(getClass().getSimpleName() + ": Error while sending logout packet to login");
        } finally {
            _accountsInGameServer.remove(account);
        }
    }

    /**
     * Adds the game server login.
     *
     * @param account the account
     * @param client  the client
     * @return {@code true} if account was not already logged in, {@code false} otherwise
     */
    public boolean addGameServerLogin(String account, L2GameClient client) {
        return _accountsInGameServer.putIfAbsent(account, client) == null;
    }

    /**
     * Send access level.
     *
     * @param account the account
     * @param level   the access level
     */
    public void sendAccessLevel(String account, int level) {
        final ChangeAccessLevel cal = new ChangeAccessLevel(account, level);
        try {
            sendPacket(cal);
        } catch (IOException e) {
        }
    }

    /**
     * Send client tracert.
     *
     * @param account the account
     * @param address the address
     */
    public void sendClientTracert(String account, String[] address) {
        final PlayerTracert ptc = new PlayerTracert(account, address[0], address[1], address[2], address[3], address[4]);
        try {
            sendPacket(ptc);
        } catch (IOException e) {
        }
    }

    /**
     * Send mail.
     *
     * @param account the account
     * @param mailId  the mail id
     * @param args    the args
     */
    public void sendMail(String account, String mailId, String... args) {
        final SendMail sem = new SendMail(account, mailId, args);
        try {
            sendPacket(sem);
        } catch (IOException e) {
        }
    }

    /**
     * Send temp ban.
     *
     * @param account the account
     * @param ip      the ip
     * @param time    the time
     */
    public void sendTempBan(String account, String ip, long time) {
        final TempBan tbn = new TempBan(account, ip, time);
        try {
            sendPacket(tbn);
        } catch (IOException e) {
        }
    }

    /**
     * Hex to string.
     *
     * @param hex the hex value
     * @return the hex value as string
     */
    private String hexToString(byte[] hex) {
        return new BigInteger(hex).toString(16);
    }

    /**
     * Kick player for the given account.
     *
     * @param account the account
     */
    private void doKickPlayer(String account) {
        final L2GameClient client = _accountsInGameServer.get(account);
        if (client != null) {
            if (client.isDetached()) {
                if (client.getActiveChar() != null) {
                    client.getActiveChar().deleteMe();
                }
            } else {
                ACCOUNTING_LOGGER.info("Kicked by login, " + client);
            }
            client.close(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_LOGGED_IN_TO_TWO_PLACES_IF_YOU_SUSPECT_ACCOUNT_THEFT_WE_RECOMMEND_CHANGING_YOUR_PASSWORD_SCANNING_YOUR_COMPUTER_FOR_VIRUSES_AND_USING_AN_ANTI_VIRUS_SOFTWARE));
        }
        sendLogout(account);
    }

    /**
     * Gets the chars on server.
     *
     * @param account the account
     */
    private void getCharsOnServer(String account) {

        int chars = 0;
        final List<Long> charToDel = new ArrayList<>();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT deletetime FROM characters WHERE account_name=?")) {
            ps.setString(1, account);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chars++;
                    final long delTime = rs.getLong("deletetime");
                    if (delTime != 0) {
                        charToDel.add(delTime);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Exception: getCharsOnServer: " + e.getMessage(), e);
        }

        final ReplyCharacters rec = new ReplyCharacters(account, chars, charToDel);
        try {
            sendPacket(rec);
        } catch (IOException e) {
        }
    }

    /**
     * Send packet.
     *
     * @param sl the sendable packet
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void sendPacket(BaseSendablePacket sl) throws IOException {
        final byte[] data = sl.getContent();
        NewCrypt.appendChecksum(data);
        _blowfish.crypt(data, 0, data.length);

        final int len = data.length + 2;
        synchronized (_out) // avoids tow threads writing in the mean time
        {
            _out.write(len & 0xff);
            _out.write((len >> 8) & 0xff);
            _out.write(data);
            _out.flush();
        }
    }

    /**
     * Gets the max player.
     *
     * @return Returns the maxPlayer.
     */
    public int getMaxPlayer() {
        return _maxPlayer;
    }

    /**
     * Sets the max player.
     *
     * @param maxPlayer The maxPlayer to set.
     */
    public void setMaxPlayer(int maxPlayer) {
        sendServerStatus(ServerStatus.MAX_PLAYERS, maxPlayer);
        _maxPlayer = maxPlayer;
    }

    /**
     * Send server status.
     *
     * @param id    the id
     * @param value the value
     */
    public void sendServerStatus(int id, int value) {
        final ServerStatus ss = new ServerStatus();
        ss.addAttribute(id, value);
        try {
            sendPacket(ss);
        } catch (IOException e) {
        }
    }

    /**
     * Send Server Type Config to LS.
     */
    public void sendServerType() {
        final ServerStatus ss = new ServerStatus();
        ss.addAttribute(ServerStatus.SERVER_TYPE, Config.SERVER_LIST_TYPE);
        try {
            sendPacket(ss);
        } catch (IOException e) {
        }
    }

    /**
     * Send change password.
     *
     * @param accountName the account name
     * @param charName    the char name
     * @param oldpass     the old pass
     * @param newpass     the new pass
     */
    public void sendChangePassword(String accountName, String charName, String oldpass, String newpass) {
        final ChangePassword cp = new ChangePassword(accountName, charName, oldpass, newpass);
        try {
            sendPacket(cp);
        } catch (IOException e) {
        }
    }

    /**
     * Gets the status string.
     *
     * @return the status string
     */
    public String getStatusString() {
        return ServerStatus.STATUS_STRING[_status];
    }

    /**
     * Gets the server name.
     *
     * @return the server name.
     */
    public String getServerName() {
        return _serverName;
    }

    /**
     * Sets the server status.
     *
     * @param status the new server status
     */
    public void setServerStatus(int status) {
        switch (status) {
            case ServerStatus.STATUS_AUTO: {
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_AUTO);
                _status = status;
                break;
            }
            case ServerStatus.STATUS_DOWN: {
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_DOWN);
                _status = status;
                break;
            }
            case ServerStatus.STATUS_FULL: {
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_FULL);
                _status = status;
                break;
            }
            case ServerStatus.STATUS_GM_ONLY: {
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_GM_ONLY);
                _status = status;
                break;
            }
            case ServerStatus.STATUS_GOOD: {
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_GOOD);
                _status = status;
                break;
            }
            case ServerStatus.STATUS_NORMAL: {
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_NORMAL);
                _status = status;
                break;
            }
            default: {
                throw new IllegalArgumentException("Status does not exists:" + status);
            }
        }
    }

    public L2GameClient getClient(String name) {
        return name != null ? _accountsInGameServer.get(name) : null;
    }

    public static class SessionKey {
        public int playOkID1;
        public int playOkID2;
        public int loginOkID1;
        public int loginOkID2;

        /**
         * Instantiates a new session key.
         *
         * @param loginOK1 the login o k1
         * @param loginOK2 the login o k2
         * @param playOK1  the play o k1
         * @param playOK2  the play o k2
         */
        public SessionKey(int loginOK1, int loginOK2, int playOK1, int playOK2) {
            playOkID1 = playOK1;
            playOkID2 = playOK2;
            loginOkID1 = loginOK1;
            loginOkID2 = loginOK2;
        }

        @Override
        public String toString() {
            return "PlayOk: " + playOkID1 + " " + playOkID2 + " LoginOk:" + loginOkID1 + " " + loginOkID2;
        }
    }

    private static class WaitingClient {
        public String account;
        public L2GameClient gameClient;
        public SessionKey session;

        /**
         * Instantiates a new waiting client.
         *
         * @param acc    the acc
         * @param client the client
         * @param key    the key
         */
        public WaitingClient(String acc, L2GameClient client, SessionKey key) {
            account = acc;
            gameClient = client;
            session = key;
        }
    }

    private static class SingletonHolder {
        protected static final LoginServerThread _instance = new LoginServerThread();
    }
}
