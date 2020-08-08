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
package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.Buffer;
import io.github.joealisson.mmocore.Client;
import io.github.joealisson.mmocore.Connection;
import org.l2j.commons.network.SessionKey;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.AccountDAO;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.data.AccountData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.xml.SecondaryAuthManager;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.enums.CharacterDeleteFailType;
import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.PlayerSelectInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.PlayerFactory;
import org.l2j.gameserver.model.holders.ClientHardwareInfoHolder;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerLogout;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipInfo;
import org.l2j.gameserver.util.FloodProtectors;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.hash;
import static org.l2j.commons.util.Util.isNotEmpty;

/**
 * Represents a client connected on Game Server.
 *
 * @author KenM
 * @author JoeAlisson
 */
public final class GameClient extends Client<Connection<GameClient>> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GameClient.class);
    protected static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");

    private final ReentrantLock activeCharLock = new ReentrantLock();

    private final FloodProtectors floodProtectors = new FloodProtectors(this);

    private final Crypt crypt;
    private String accountName;
    private SessionKey sessionId;
    private Player player;
    private ClientHardwareInfoHolder hardwareInfo;
    private boolean isAuthedGG;
    private List<PlayerSelectInfo> playersInfo;

    private boolean _protocol;

    private int[][] trace;

    private ConnectionState state;
    private AccountData account;
    private boolean secondaryAuthed;
    private int activeSlot = -1;

    public
    GameClient(Connection<GameClient> connection) {
        super(connection);
        crypt = new Crypt();
    }

    @Override
    public boolean encrypt(Buffer data, int offset, int size) {
        return crypt.encrypt(data, offset, size);
    }

    @Override
    public boolean decrypt(Buffer data, int offset, int size) {
        return crypt.decrypt(data, offset, size);
    }

    @Override
    protected void onDisconnection() {
        LOGGER_ACCOUNTING.debug("Client Disconnected: {}", this);

        if(nonNull(getAccountName())) {
            if (state == ConnectionState.AUTHENTICATED) {
                AuthServerCommunication.getInstance().removeAuthedClient(getAccountName());
            } else {
                AuthServerCommunication.getInstance().removeWaitingClient(getAccountName());
            }
            AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(getAccountName()));
        }

        Disconnection.of(this).onDisconnection();
    }

    @Override
    public void onConnected() {
        setConnectionState(ConnectionState.CONNECTED);
        LOGGER_ACCOUNTING.debug("Client Connected: {}", this);
    }

    public void close(boolean toLoginScreen) {
        sendPacket(toLoginScreen ? ServerClose.STATIC_PACKET : LeaveWorld.STATIC_PACKET);
    }

    public byte[] enableCrypt() {
        final byte[] key = BlowFishKeygen.getRandomKey();
        crypt.setKey(key);
        return key;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ReentrantLock getActivePlayerLock() {
        return activeCharLock;
    }

    public FloodProtectors getFloodProtectors() {
        return floodProtectors;
    }

    public void setGameGuardOk(boolean val) {
        isAuthedGG = val;
    }

    public String getAccountName() {
        return accountName;
    }

    public synchronized void setAccountName(String accountName) {
        this.accountName = accountName;

        account = getDAO(AccountDAO.class).findById(this.accountName);
        if(isNull(account)) {
            createNewAccountData();
        }
    }

    public SessionKey getSessionId() {
        return sessionId;
    }

    public void setSessionId(SessionKey sk) {
        sessionId = sk;
    }

    public void sendPacket(ServerPacket packet) {
        if (isNull(packet)) {
            return;
        }

        writePacket(packet);
        packet.runImpl(player);
    }


    public void sendPacket(SystemMessageId smId) {
        sendPacket(SystemMessage.getSystemMessage(smId));
    }

    public CharacterDeleteFailType markToDeleteChar(int slot) {
        PlayerSelectInfo info = getPlayerSelection(slot);
        if (isNull(info)) {
            return CharacterDeleteFailType.UNKNOWN;
        }

        if (MentorManager.getInstance().isMentor(info.getObjectId())) {
            return CharacterDeleteFailType.MENTOR;
        } else if (MentorManager.getInstance().isMentee(info.getObjectId())) {
            return CharacterDeleteFailType.MENTEE;
        } else if (CommissionManager.getInstance().hasCommissionItems(info.getObjectId())) {
            return CharacterDeleteFailType.COMMISSION;
        } else if (MailEngine.getInstance().hasMailInProgress(info.getObjectId())) {
            return CharacterDeleteFailType.MAIL;
        } else {
            final int clanId = PlayerNameTable.getInstance().getClassIdById(info.getObjectId());
            if (clanId > 0) {
                final Clan clan = ClanTable.getInstance().getClan(clanId);
                if (clan != null) {
                    if (clan.getLeaderId() == info.getObjectId()) {
                        return CharacterDeleteFailType.PLEDGE_MASTER;
                    }
                    return CharacterDeleteFailType.PLEDGE_MEMBER;
                }
            }
        }

        if (Config.DELETE_DAYS == 0) {
            PlayerFactory.deleteCharByObjId(info.getObjectId());
            playersInfo.remove(slot);
        } else {
            var deleteTime = Duration.ofDays(Config.DELETE_DAYS).toMillis() + System.currentTimeMillis();
            info.setDeleteTime(deleteTime);
            getDAO(PlayerDAO.class).updateDeleteTime(info.getObjectId(), deleteTime);
        }

        LOGGER_ACCOUNTING.info("{} deleted {}", this, info.getObjectId());
        return CharacterDeleteFailType.NONE;
    }

    public void restore(int characterSlot) {
        final int objectId = getObjectIdForSlot(characterSlot);
        if (objectId < 0) {
            return;
        }

        getDAO(PlayerDAO.class).updateDeleteTime(objectId, 0);
        LOGGER_ACCOUNTING.info("Restore {} [{}]", objectId, this);
    }

    public Player load(int slot) {
        final int objectId = getObjectIdForSlot(slot);
        if (objectId < 0) {
            return null;
        }

        Player player = World.getInstance().findPlayer(objectId);
        if (player != null) {
            // exploit prevention, should not happens in normal way
            if (player.isOnline()) {
                LOGGER.error("Attempt of double login: {} ({}) {}", player.getName(), objectId, accountName);
            }
            if (player.getClient() != null)
            {
                Disconnection.of(player).defaultSequence(false);
            }
            else
            {
                player.storeMe();
                player.deleteMe();
            }
            return null;
        }

        player = PlayerFactory.loadPlayer(this, objectId);
        if (player == null) {
            LOGGER.error("Could not restore in slot: {}", slot);
        }
        activeSlot = slot;
        return player;
    }

    public PlayerSelectInfo getPlayerSelection(int slot) {
        if (isNull(playersInfo) || slot < 0 || slot >= playersInfo.size()) {
            return null;
        }
        return playersInfo.get(slot);
    }

    private int getObjectIdForSlot(int slot) {
        final PlayerSelectInfo info = getPlayerSelection(slot);
        if (info == null) {
            LOGGER.warn("{} tried select in slot {} but no characters exits at that slot.", this, slot);
            return -1;
        }
        return info.getObjectId();
    }

    private AccountData getAccountData() {
        return account;
    }

    private void createNewAccountData() {
        account = new AccountData();
        account.setAccount(accountName);
    }

    public void sendActionFailed() {
        sendPacket(ActionFailed.STATIC_PACKET);
    }

    public boolean isProtocolOk() {
        return _protocol;
    }

    public void setProtocolOk(boolean b) {
        _protocol = b;
    }

    public void setClientTracert(int[][] tracert) {
        trace = tracert;
    }

    public int[][] getTrace() {
        return trace;
    }

    public ClientHardwareInfoHolder getHardwareInfo() {
        return hardwareInfo;
    }

    public void setHardwareInfo(ClientHardwareInfoHolder hardwareInfo) {
        this.hardwareInfo = hardwareInfo;
    }

    public ConnectionState getConnectionState() {
        return state;
    }

    public void setConnectionState(ConnectionState state) {
        this.state = state;
    }

    public long getVipPoints() {
        return getAccountData().getVipPoints();
    }

    public long getVipTierExpiration() {
        return getAccountData().getVipTierExpiration();
    }

    public void storeAccountData() {
        getDAO(AccountDAO.class).save(getAccountData());
    }

    public void updateVipPoints(long points) {
        if(points == 0) {
            return;
        }
        var currentVipTier = VipEngine.getInstance().getVipTier(getVipPoints());
        getAccountData().updateVipPoints(points);
        var newTier = VipEngine.getInstance().getVipTier(getVipPoints());
        if(newTier != currentVipTier && nonNull(player)) {
            player.setVipTier(newTier);
            if(newTier > 0) {
                getAccountData().setVipTierExpiration(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());
                VipEngine.getInstance().manageTier(player);
            } else {
                getAccountData().setVipTierExpiration(0);
            }
        }
        sendPacket(new ReceiveVipInfo());
    }

    public int getCoin() {
        return getAccountData().getCoin();
    }

    public void updateCoin(int coins) {
        getAccountData().updateCoins(coins);
    }

    public void setCoin(int coins) {
        getAccountData().setCoins(coins);
    }

    public void setVipTierExpiration(long expiration) {
        getAccountData().setVipTierExpiration(expiration);
    }

    @Override
    public String toString() {
        try {
            final String address = getHostAddress();
            final ConnectionState state = getConnectionState();
            return switch (state) {
                case CONNECTED, CLOSING, DISCONNECTED  -> "[IP: " + (address == null ? "disconnected" : address) + "]";
                case AUTHENTICATED -> "[Account: " + accountName + " - IP: " + (address == null ? "disconnected" : address) + "]";
                case IN_GAME, JOINING_GAME -> "[Player: " + (player == null ? "disconnected" : player.getName() + "[" + player.getObjectId() + "]") + " - Account: " + accountName + " - IP: " + (address == null ? "disconnected" : address) + "]";
            };
        } catch (NullPointerException e) {
            return "[Character read failed due to disconnect]";
        }
    }

    public boolean hasSecondPassword() {
        return isNotEmpty(account.getSecAuthPassword());
    }

    public boolean saveSecondPassword(String password) {
        if (hasSecondPassword()) {
            LOGGER.warn("{} forced savePassword", this);
            Disconnection.of(this).defaultSequence(false);
            return false;
        }

        if (!validatePassword(password)) {
            sendPacket(new Ex2ndPasswordAck(0, Ex2ndPasswordAck.WRONG_PATTERN));
            return false;
        }

        try {
            var cripted = hash(password);
            account.setSecAuthPassword(cripted);
            account.setSecAuthAttempts(0);
            return true;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Unsupported Algorithm", e);
        }
        return false;
    }

    private boolean validatePassword(String password) {
        if (!Util.isInteger(password)) {
            return false;
        }

        if ((password.length() < 6) || (password.length() > 8)) {
            return false;
        }

        return !SecondaryAuthManager.getInstance().isForbiddenPassword(Integer.parseInt(password));
    }

    public boolean changeSecondPassword(String password, String newPassword) {
        if (!hasSecondPassword()) {
            LOGGER.warn("{} forced changePassword", this);
            Disconnection.of(this).defaultSequence(false);
            return false;
        }

        if (!checkPassword(password, true)) {
            return false;
        }

        if (!validatePassword(newPassword)) {
            sendPacket(new Ex2ndPasswordAck(2, Ex2ndPasswordAck.WRONG_PATTERN));
            return false;
        }

        try {
            account.setSecAuthPassword(hash(newPassword));
            secondaryAuthed = false;
            return true;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Unsupported Algorithm", e);
        }
        return false;
    }

    public boolean checkPassword(String password, boolean skipAuth) {
        try {
            password = hash(password);

            if (!Objects.equals(password, account.getSecAuthPassword())) {
                var attempts = account.increaseSecAuthAttempts();
                if (attempts  < SecondaryAuthManager.getInstance().getMaxAttempts()) {
                    sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_WRONG, attempts));
                } else {
                    // TODO AuthServerCommunication.getInstance().sendTempBan(_activeClient.getAccountName(), _activeClient.getHostAddress(), SecondaryAuthManager.getInstance().getBanTime());
                    //AuthServerCommunication.getInstance().sendMail(_activeClient.getAccountName(), "SATempBan", _activeClient.getHostAddress(), Integer.toString(SecondaryAuthManager.getInstance().getMaxAttempts()), Long.toString(SecondaryAuthManager.getInstance().getBanTime()), SecondaryAuthManager.getInstance().getRecoveryLink());
                    close(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_BAN, SecondaryAuthManager.getInstance().getMaxAttempts()));
                    LOGGER.warn("{}  has inputted the wrong password {} times in row.", this, attempts);
                }
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Unsupported Algorithm", e);
            return false;
        }

        if (!skipAuth) {
            secondaryAuthed = true;
            sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_OK, account.getSecAuthAttempts()));
        }
        account.setSecAuthAttempts(0);
        return true;
    }

    public boolean isSecondaryAuthed() {
        return secondaryAuthed;
    }

    public void openSecondaryAuthDialog() {
        if (hasSecondPassword()) {
            sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_PROMPT));
        } else {
            sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_NEW));
        }
    }

    public int getPlayerCount() {
        return nonNull(playersInfo) ? playersInfo.size() : 0;
    }

    public List<PlayerSelectInfo> getPlayersInfo() {
        if(isNull(playersInfo)) {
            synchronized (this) {
                if(isNull(playersInfo)) {
                    playersInfo = PlayerFactory.loadPlayersInfo(this);
                }
            }
        }
        return playersInfo;
    }

    public void addPlayerInfo(PlayerSelectInfo playerInfo) {
        activeSlot = playersInfo.size();
        playersInfo.add(playerInfo);
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    public void reloadActivePlayerInfo() {
        PlayerSelectInfo current = playersInfo.remove(activeSlot);
        if(nonNull(current)) {
            playersInfo.add(activeSlot, new PlayerSelectInfo(getDAO(PlayerDAO.class).findById(current.getObjectId())));
        }
    }

    public int getPlayerInfoAccessLevel(int playerId) {
        for (PlayerSelectInfo info : playersInfo) {
            if(info.getObjectId() == playerId) {
                return info.getAccessLevel();
            }
        }
        throw new IllegalStateException("There is no info of player " + playerId);
    }
}
