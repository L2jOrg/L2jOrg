package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.Client;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.network.SessionKey;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.AccountDAO;
import org.l2j.gameserver.data.database.model.AccountData;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.SecondaryAuthData;
import org.l2j.gameserver.enums.CharacterDeleteFailType;
import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.holders.ClientHardwareInfoHolder;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerLogout;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.security.SecondaryPasswordAuth;
import org.l2j.gameserver.util.FloodProtectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * Represents a client connected on Game Server.
 *
 * @author KenM
 */
public final class L2GameClient extends Client<io.github.joealisson.mmocore.Connection<L2GameClient>> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(L2GameClient.class);
    protected static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");

    private final ReentrantLock _activeCharLock = new ReentrantLock();
    // flood protectors
    private final FloodProtectors _floodProtectors = new FloodProtectors(this);
    // Crypt
    private final Crypt _crypt;
    private String accountName;
    private SessionKey _sessionId;
    private L2PcInstance _activeChar;
    private SecondaryPasswordAuth _secondaryAuth;
    private ClientHardwareInfoHolder _hardwareInfo;
    private boolean _isAuthedGG;
    private CharSelectInfoPackage[] _charSlotMapping = null;
    private volatile boolean _isDetached = false;

    private boolean _protocol;

    private int[][] trace;

    private ConnectionState state;
    private AccountData account;

    public L2GameClient(io.github.joealisson.mmocore.Connection<L2GameClient> connection) {
        super(connection);
        _crypt = new Crypt(this);
    }

    public static void deleteCharByObjId(int objid) {
        if (objid < 0) {
            return;
        }

        CharNameTable.getInstance().removeName(objid);

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_contacts WHERE charId=? OR contactId=?")) {
                ps.setInt(1, objid);
                ps.setInt(2, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_friends WHERE charId=? OR friendId=?")) {
                ps.setInt(1, objid);
                ps.setInt(2, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_hennas WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_macroses WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_quests WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_recipebook WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_shortcuts WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills_save WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_subclasses WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM heroes WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM olympiad_nobles WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM pets WHERE item_obj_id IN (SELECT object_id FROM items WHERE items.owner_id=?)")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_variations WHERE itemId IN (SELECT object_id FROM items WHERE items.owner_id=?)")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_special_abilities WHERE objectId IN (SELECT object_id FROM items WHERE items.owner_id=?)")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_variables WHERE id IN (SELECT object_id FROM items WHERE items.owner_id=?)")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM items WHERE owner_id=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM merchant_lease WHERE player_id=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_reco_bonus WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_instance_time WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM characters WHERE charId=?")) {
                ps.setInt(1, objid);
                ps.execute();
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting character.", e);
        }
    }

    @Override
    public int encrypt(byte[] data, int offset, int size) {
        _crypt.encrypt(data, offset, size);
        return size;
    }

    @Override
    public boolean decrypt(byte[] data, int offset, int size) {
        return _crypt.decrypt(data, offset, size);
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

        if ((_activeChar == null) || !_activeChar.isInOfflineMode()) {
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

    public void close(IClientOutgoingPacket packet) {
        super.close(packet);
    }

    public void close(boolean toLoginScreen) {
        close(toLoginScreen ? ServerClose.STATIC_PACKET : LeaveWorld.STATIC_PACKET);
    }

    public byte[] enableCrypt() {
        final byte[] key = BlowFishKeygen.getRandomKey();
        _crypt.setKey(key);
        return key;
    }

    public L2PcInstance getActiveChar() {
        return _activeChar;
    }

    public void setActiveChar(L2PcInstance activeChar) {
        _activeChar = activeChar;
    }

    public ReentrantLock getActiveCharLock() {
        return _activeCharLock;
    }

    public FloodProtectors getFloodProtectors() {
        return _floodProtectors;
    }

    public void setGameGuardOk(boolean val) {
        _isAuthedGG = val;
    }

    public boolean isAuthedGG() {
        return _isAuthedGG;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String account) {
        accountName = account;

        if (SecondaryAuthData.getInstance().isEnabled()) {
            _secondaryAuth = new SecondaryPasswordAuth(this);
        }
    }

    public SessionKey getSessionId() {
        return _sessionId;
    }

    public void setSessionId(SessionKey sk) {
        _sessionId = sk;
    }

    public void sendPacket(IClientOutgoingPacket packet) {
        if (_isDetached || (packet == null)) {
            return;
        }

        writePacket(packet);
        packet.runImpl(_activeChar);
    }


    public void sendPacket(SystemMessageId smId) {
        sendPacket(SystemMessage.getSystemMessage(smId));
    }

    public boolean isDetached() {
        return _isDetached;
    }

    public void setDetached(boolean b) {
        _isDetached = b;
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
        } else if (MailManager.getInstance().getMailsInProgress(objectId) > 0) {
            return CharacterDeleteFailType.MAIL;
        } else {
            final int clanId = CharNameTable.getInstance().getClassIdById(objectId);
            if (clanId > 0) {
                final L2Clan clan = ClanTable.getInstance().getClan(clanId);
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
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps2 = con.prepareStatement("UPDATE characters SET deletetime=? WHERE charId=?")) {
                ps2.setLong(1, System.currentTimeMillis() + (Config.DELETE_DAYS * 86400000)); // 24*60*60*1000 = 86400000
                ps2.setInt(2, objectId);
                ps2.execute();
            } catch (SQLException e) {
                LOGGER.warn("Failed to update char delete time: ", e);
            }
        }

        LOGGER_ACCOUNTING.info("Delete, " + objectId + ", " + this);
        return CharacterDeleteFailType.NONE;
    }

    public void restore(int characterSlot) {
        final int objectId = getObjectIdForSlot(characterSlot);
        if (objectId < 0) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE charId=?")) {
            statement.setInt(1, objectId);
            statement.execute();
        } catch (Exception e) {
            LOGGER.error("Error restoring character.", e);
        }

        LOGGER_ACCOUNTING.info("Restore, " + objectId + ", " + this);
    }

    public L2PcInstance load(int characterSlot) {
        final int objectId = getObjectIdForSlot(characterSlot);
        if (objectId < 0) {
            return null;
        }

        L2PcInstance player = L2World.getInstance().getPlayer(objectId);
        if (player != null) {
            // exploit prevention, should not happens in normal way
            if (player.isOnlineInt() == 1) {
                LOGGER.error("Attempt of double login: {} ({}) {}", player.getName(), objectId, accountName);
            }
            Disconnection.of(player).defaultSequence(false);
            return null;
        }

        player = L2PcInstance.load(objectId);
        if (player == null) {
            LOGGER.error("Could not restore in slot: {}", characterSlot);
        }

        return player;
    }

    public void setCharSelection(CharSelectInfoPackage[] chars) {
        _charSlotMapping = chars;
    }

    public CharSelectInfoPackage getCharSelection(int charslot) {
        if ((_charSlotMapping == null) || (charslot < 0) || (charslot >= _charSlotMapping.length)) {
            return null;
        }
        return _charSlotMapping[charslot];
    }

    public SecondaryPasswordAuth getSecondaryAuth() {
        return _secondaryAuth;
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
        if(isNull(account)) {
            account = getDAO(AccountDAO.class).findById(accountName);
            if(isNull(account)) {
               createNewAccountData();
            }
        }
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
        return _crypt;
    }

    public ClientHardwareInfoHolder getHardwareInfo() {
        return _hardwareInfo;
    }

    public void setHardwareInfo(ClientHardwareInfoHolder hardwareInfo) {
        _hardwareInfo = hardwareInfo;
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
        getAccountData().updateVipPoints(points);
    }

    public int getCoin() {
        return getAccountData().getCoin();
    }

    public void updateCoin(int coins) {
        getAccountData().updateCoins(coins);
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
                case IN_GAME, JOINING_GAME -> "[Character: " + (_activeChar == null ? "disconnected" : _activeChar.getName() + "[" + _activeChar.getObjectId() + "]") + " - Account: " + accountName + " - IP: " + (address == null ? "disconnected" : address) + "]";
            };
        } catch (NullPointerException e) {
            return "[Character read failed due to disconnect]";
        }
    }
}
