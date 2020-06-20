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

import io.github.joealisson.mmocore.Client;
import io.github.joealisson.mmocore.Connection;
import org.l2j.commons.network.SessionKey;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.AccountDAO;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.dao.PetDAO;
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
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    private CharSelectInfoPackage[] charSlotMapping = null;
    private volatile boolean isDetached = false;

    private boolean _protocol;

    private int[][] trace;

    private ConnectionState state;
    private AccountData account;
    private boolean secondaryAuthed;

    public GameClient(Connection<GameClient> connection) {
        super(connection);
        crypt = new Crypt(this);
    }

    public static void deleteCharByObjId(int objId) {
        if (objId < 0) {
            return;
        }

        PlayerNameTable.getInstance().removeName(objId);
        getDAO(PetDAO.class).deleteByOwner(objId);

        var itemDAO = getDAO(ItemDAO.class);
        itemDAO.deleteVariationsByOwner(objId);
        itemDAO.deleteSpecialAbilitiesByOwner(objId);
        itemDAO.deleteByOwner(objId);
        getDAO(PlayerDAO.class).deleteById(objId);
    }

    @Override
    public int encryptedSize(int dataSize) {
        return dataSize;
    }
    @Override
    public byte[] encrypt(byte[] data, int offset, int size) {
        return crypt.encrypt(data, offset, size);
    }

    @Override
    public boolean decrypt(byte[] data, int offset, int size) {
        return crypt.decrypt(data, offset, size);
    }

    @Override
    protected void onDisconnection() {
        LOGGER_ACCOUNTING.debug("Client Disconnected: {}", this);

        if(state == ConnectionState.AUTHENTICATED) {
            AuthServerCommunication.getInstance().removeAuthedClient(getAccountName());
        } else {
            AuthServerCommunication.getInstance().removeWaitingClient(getAccountName());
        }
        AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(getAccountName()));

        if ((player == null) || !player.isInOfflineMode()) {
            Disconnection.of(this).onDisconnection();
        }
    }

    @Override
    public void onConnected() {
        setConnectionState(ConnectionState.CONNECTED);
        LOGGER_ACCOUNTING.debug("Client Connected: {}", this);
    }

    public void closeNow() {
        super.close(null);
    }

    public void close(ServerPacket packet) {
        super.close(packet);
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

    public boolean isAuthedGG() {
        return isAuthedGG;
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
        if (isDetached || isNull(packet)) {
            return;
        }

        writePacket(packet);
        packet.runImpl(player);
    }


    public void sendPacket(SystemMessageId smId) {
        sendPacket(SystemMessage.getSystemMessage(smId));
    }

    public boolean isDetached() {
        return isDetached;
    }

    public void setDetached(boolean b) {
        isDetached = b;
    }

    /**
     * Method to handle character deletion
     *
     * @param characterSlot
     * @return a byte:
     * <li>-1: Error: No char was found for such charslot, caught exception, etc...
     * <li>0: character is not member of any clan, proceed with deletion
     * <li>1: character is member of a clan, but not clan leader
     * <li>2: character is clan leader
     */
    public CharacterDeleteFailType markToDeleteChar(int characterSlot) {
        final int objectId = getObjectIdForSlot(characterSlot);
        if (objectId < 0) {
            return CharacterDeleteFailType.UNKNOWN;
        }

        if (MentorManager.getInstance().isMentor(objectId)) {
            return CharacterDeleteFailType.MENTOR;
        } else if (MentorManager.getInstance().isMentee(objectId)) {
            return CharacterDeleteFailType.MENTEE;
        } else if (CommissionManager.getInstance().hasCommissionItems(objectId)) {
            return CharacterDeleteFailType.COMMISSION;
        } else if (MailEngine.getInstance().hasMailInProgress(objectId)) {
            return CharacterDeleteFailType.MAIL;
        } else {
            final int clanId = PlayerNameTable.getInstance().getClassIdById(objectId);
            if (clanId > 0) {
                final Clan clan = ClanTable.getInstance().getClan(clanId);
                if (clan != null) {
                    if (clan.getLeaderId() == objectId) {
                        return CharacterDeleteFailType.PLEDGE_MASTER;
                    }
                    return CharacterDeleteFailType.PLEDGE_MEMBER;
                }
            }
        }

        if (Config.DELETE_DAYS == 0) {
            deleteCharByObjId(objectId);
        } else {
            getDAO(PlayerDAO.class).updateDeleteTime(objectId, System.currentTimeMillis() + (Config.DELETE_DAYS * 86400000));
        }

        LOGGER_ACCOUNTING.info("Delete, " + objectId + ", " + this);
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

    public Player load(int characterSlot) {
        final int objectId = getObjectIdForSlot(characterSlot);
        if (objectId < 0) {
            return null;
        }

        Player player = World.getInstance().findPlayer(objectId);
        if (player != null) {
            // exploit prevention, should not happens in normal way
            if (player.isOnlineInt() == 1) {
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

        player = Player.load(objectId);
        if (player == null) {
            LOGGER.error("Could not restore in slot: {}", characterSlot);
        }

        return player;
    }

    public void setCharSelection(CharSelectInfoPackage[] chars) {
        charSlotMapping = chars;
    }

    public CharSelectInfoPackage getCharSelection(int charslot) {
        if ((charSlotMapping == null) || (charslot < 0) || (charslot >= charSlotMapping.length)) {
            return null;
        }
        return charSlotMapping[charslot];
    }

    private int getObjectIdForSlot(int characterSlot) {
        final CharSelectInfoPackage info = getCharSelection(characterSlot);
        if (info == null) {
            LOGGER.warn("{} tried to delete Character in slot {} but no characters exits at that slot.", this, characterSlot);
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

    public Crypt getCrypt() {
        return crypt;
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
}
