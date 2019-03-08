package org.l2j.gameserver.mobius.gameserver.network;

import io.github.joealisson.mmocore.Client;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.LoginServerThread;
import org.l2j.gameserver.mobius.gameserver.LoginServerThread.SessionKey;
import org.l2j.gameserver.mobius.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SecondaryAuthData;
import org.l2j.gameserver.mobius.gameserver.enums.CharacterDeleteFailType;
import org.l2j.gameserver.mobius.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.mobius.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.mobius.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.ClientHardwareInfoHolder;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.*;
import org.l2j.gameserver.mobius.gameserver.security.SecondaryPasswordAuth;
import org.l2j.gameserver.mobius.gameserver.util.FloodProtectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.nio.channels.Channel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * Represents a client connected on Game Server.
 * @author KenM
 */
public final class L2GameClient extends Client<io.github.joealisson.mmocore.Connection<L2GameClient>>
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(L2GameClient.class);
	protected static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
	
	private final int _objectId;

	private String _accountName;
	private SessionKey _sessionId;
	private L2PcInstance _activeChar;
	private final ReentrantLock _activeCharLock = new ReentrantLock();
	private SecondaryPasswordAuth _secondaryAuth;
	private ClientHardwareInfoHolder _hardwareInfo;
	private boolean _isAuthedGG;
	private CharSelectInfoPackage[] _charSlotMapping = null;
	
	// flood protectors
	private final FloodProtectors _floodProtectors = new FloodProtectors(this);
	
	// Crypt
	private final Crypt _crypt;
	
	private volatile boolean _isDetached = false;
	
	private boolean _protocol;
	
	private int[][] trace;

	private ConnectionState state;

	public L2GameClient(io.github.joealisson.mmocore.Connection<L2GameClient> connection) {
		super(connection);
		_objectId = IdFactory.getInstance().getNextId();
		_crypt = new Crypt(this);
	}

    @Override
    public int encrypt(byte[] data, int offset, int size) {
        return 0;
    }

    @Override
    public boolean decrypt(byte[] data, int offset, int size) {
        return false;
    }

    @Override
    protected void onDisconnection() {
        LOGGER_ACCOUNTING.debug("Client Disconnected: {}", this);

        LoginServerThread.getInstance().sendLogout(getAccountName());

        if ((_activeChar == null) || !_activeChar.isInOfflineMode()) {
            IdFactory.getInstance().releaseId(getObjectId());
            Disconnection.of(this).onDisconnection();
        }
    }

    @Override
    public void onConnected() {
        setConnectionState(ConnectionState.CONNECTED);
        LOGGER_ACCOUNTING.debug("Client Connected: {}", this);
    }

    public void setConnectionState(ConnectionState state) {
        this.state = state;
    }

    public int getObjectId()
	{
		return _objectId;
	}

	
	public void closeNow() {
	    super.close(null);
	}
	
	public void close(IClientOutgoingPacket packet) {
	    super.close(packet);
	}
	
	public void close(boolean toLoginScreen)
	{
		close(toLoginScreen ? ServerClose.STATIC_PACKET : LeaveWorld.STATIC_PACKET);
	}
	
	public byte[] enableCrypt()
	{
		final byte[] key = BlowFishKeygen.getRandomKey();
		_crypt.setKey(key);
		return key;
	}
	
	/**
	 * For loaded offline traders returns localhost address.
	 * @return cached connection IP address, for checking detached clients.
	 */
	public InetAddress getConnectionAddress()
	{
		return address;
	}
	
	public L2PcInstance getActiveChar()
	{
		return _activeChar;
	}
	
	public void setActiveChar(L2PcInstance activeChar)
	{
		_activeChar = activeChar;
	}
	
	public ReentrantLock getActiveCharLock()
	{
		return _activeCharLock;
	}
	
	public FloodProtectors getFloodProtectors()
	{
		return _floodProtectors;
	}
	
	public void setGameGuardOk(boolean val)
	{
		_isAuthedGG = val;
	}
	
	public boolean isAuthedGG()
	{
		return _isAuthedGG;
	}
	
	public void setAccountName(String activeChar)
	{
		_accountName = activeChar;
		
		if (SecondaryAuthData.getInstance().isEnabled())
		{
			_secondaryAuth = new SecondaryPasswordAuth(this);
		}
	}
	
	public String getAccountName()
	{
		return _accountName;
	}
	
	public void setSessionId(SessionKey sk)
	{
		_sessionId = sk;
	}
	
	public SessionKey getSessionId()
	{
		return _sessionId;
	}
	
	public void sendPacket(IClientOutgoingPacket packet)
	{
		if (_isDetached || (packet == null))
		{
			return;
		}

		writePacket(packet);
		packet.runImpl(_activeChar);
	}
	
	/**
	 * @param smId
	 */
	public void sendPacket(SystemMessageId smId)
	{
		sendPacket(SystemMessage.getSystemMessage(smId));
	}
	
	public boolean isDetached()
	{
		return _isDetached;
	}
	
	public void setDetached(boolean b)
	{
		_isDetached = b;
	}
	
	/**
	 * Method to handle character deletion
	 * @param characterSlot
	 * @return a byte:
	 *         <li>-1: Error: No char was found for such charslot, caught exception, etc...
	 *         <li>0: character is not member of any clan, proceed with deletion
	 *         <li>1: character is member of a clan, but not clan leader
	 *         <li>2: character is clan leader
	 */
	public CharacterDeleteFailType markToDeleteChar(int characterSlot)
	{
		final int objectId = getObjectIdForSlot(characterSlot);
		if (objectId < 0)
		{
			return CharacterDeleteFailType.UNKNOWN;
		}
		
		if (MentorManager.getInstance().isMentor(objectId))
		{
			return CharacterDeleteFailType.MENTOR;
		}
		else if (MentorManager.getInstance().isMentee(objectId))
		{
			return CharacterDeleteFailType.MENTEE;
		}
		else if (CommissionManager.getInstance().hasCommissionItems(objectId))
		{
			return CharacterDeleteFailType.COMMISSION;
		}
		else if (MailManager.getInstance().getMailsInProgress(objectId) > 0)
		{
			return CharacterDeleteFailType.MAIL;
		}
		else
		{
			final int clanId = CharNameTable.getInstance().getClassIdById(objectId);
			if (clanId > 0)
			{
				final L2Clan clan = ClanTable.getInstance().getClan(clanId);
				if (clan != null)
				{
					if (clan.getLeaderId() == objectId)
					{
						return CharacterDeleteFailType.PLEDGE_MASTER;
					}
					return CharacterDeleteFailType.PLEDGE_MEMBER;
				}
			}
		}
		
		if (Config.DELETE_DAYS == 0)
		{
			deleteCharByObjId(objectId);
		}
		else
		{
			try (Connection con = DatabaseFactory.getInstance().getConnection();
				 PreparedStatement ps2 = con.prepareStatement("UPDATE characters SET deletetime=? WHERE charId=?"))
			{
				ps2.setLong(1, System.currentTimeMillis() + (Config.DELETE_DAYS * 86400000)); // 24*60*60*1000 = 86400000
				ps2.setInt(2, objectId);
				ps2.execute();
			}
			catch (SQLException e)
			{
				LOGGER.warn("Failed to update char delete time: ", e);
			}
		}
		
		LOGGER_ACCOUNTING.info("Delete, " + objectId + ", " + this);
		return CharacterDeleteFailType.NONE;
	}
	
	public void restore(int characterSlot)
	{
		final int objectId = getObjectIdForSlot(characterSlot);
		if (objectId < 0)
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE charId=?"))
		{
			statement.setInt(1, objectId);
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Error restoring character.", e);
		}
		
		LOGGER_ACCOUNTING.info("Restore, " + objectId + ", " + this);
	}
	
	public static void deleteCharByObjId(int objid)
	{
		if (objid < 0)
		{
			return;
		}
		
		CharNameTable.getInstance().removeName(objid);
		
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_contacts WHERE charId=? OR contactId=?"))
			{
				ps.setInt(1, objid);
				ps.setInt(2, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_friends WHERE charId=? OR friendId=?"))
			{
				ps.setInt(1, objid);
				ps.setInt(2, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_hennas WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_macroses WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_quests WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_recipebook WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_shortcuts WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills_save WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_subclasses WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM heroes WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM olympiad_nobles WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM pets WHERE item_obj_id IN (SELECT object_id FROM items WHERE items.owner_id=?)"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_variations WHERE itemId IN (SELECT object_id FROM items WHERE items.owner_id=?)"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_special_abilities WHERE objectId IN (SELECT object_id FROM items WHERE items.owner_id=?)"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_variables WHERE id IN (SELECT object_id FROM items WHERE items.owner_id=?)"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM items WHERE owner_id=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM merchant_lease WHERE player_id=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_reco_bonus WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_instance_time WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM characters WHERE charId=?"))
			{
				ps.setInt(1, objid);
				ps.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Error deleting character.", e);
		}
	}
	
	public L2PcInstance load(int characterSlot)
	{
		final int objectId = getObjectIdForSlot(characterSlot);
		if (objectId < 0)
		{
			return null;
		}
		
		L2PcInstance player = L2World.getInstance().getPlayer(objectId);
		if (player != null)
		{
			// exploit prevention, should not happens in normal way
			if (player.isOnlineInt() == 1)
			{
				LOGGER.error("Attempt of double login: {} ({}) {}", player.getName(), objectId, _accountName);
			}
			Disconnection.of(player).defaultSequence(false);
			return null;
		}
		
		player = L2PcInstance.load(objectId);
		if (player == null)
		{
			LOGGER.error("Could not restore in slot: {}", characterSlot);
		}
		
		return player;
	}
	
	/**
	 * @param chars
	 */
	public void setCharSelection(CharSelectInfoPackage[] chars)
	{
		_charSlotMapping = chars;
	}
	
	public CharSelectInfoPackage getCharSelection(int charslot)
	{
		if ((_charSlotMapping == null) || (charslot < 0) || (charslot >= _charSlotMapping.length))
		{
			return null;
		}
		return _charSlotMapping[charslot];
	}
	
	public SecondaryPasswordAuth getSecondaryAuth()
	{
		return _secondaryAuth;
	}
	
	/**
	 * @param characterSlot
	 * @return
	 */
	private int getObjectIdForSlot(int characterSlot)
	{
		final CharSelectInfoPackage info = getCharSelection(characterSlot);
		if (info == null)
		{
			LOGGER.warn("{} tried to delete Character in slot {} but no characters exits at that slot.", this, characterSlot);
			return -1;
		}
		return info.getObjectId();
	}
	
	/**
	 * Produces the best possible string representation of this client.
	 */
	@Override
	public String toString() {
		try {
			final String address = getHostAddress();
			final ConnectionState state = getConnectionState();
			switch (state) {
				case CONNECTED: {
					return "[IP: " + (address == null ? "disconnected" : address) + "]";
				}
				case AUTHENTICATED:
				{
					return "[Account: " + _accountName + " - IP: " + (address == null ? "disconnected" : address) + "]";
				}
				case IN_GAME:
				{
					return "[Character: " + (_activeChar == null ? "disconnected" : _activeChar.getName() + "[" + _activeChar.getObjectId() + "]") + " - Account: " + _accountName + " - IP: " + (address == null ? "disconnected" : address) + "]";
				}
				default:
				{
					throw new IllegalStateException("Missing state on switch");
				}
			}
		}
		catch (NullPointerException e)
		{
			return "[Character read failed due to disconnect]";
		}
	}
	
	public boolean isProtocolOk()
	{
		return _protocol;
	}
	
	public void setProtocolOk(boolean b)
	{
		_protocol = b;
	}
	
	public void setClientTracert(int[][] tracert)
	{
		trace = tracert;
	}
	
	public int[][] getTrace()
	{
		return trace;
	}
	
	public void sendActionFailed()
	{
		sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public ICrypt getCrypt()
	{
		return _crypt;
	}
	
	/**
	 * @return the hardwareInfo
	 */
	public ClientHardwareInfoHolder getHardwareInfo()
	{
		return _hardwareInfo;
	}
	
	/**
	 * @param hardwareInfo
	 */
	public void setHardwareInfo(ClientHardwareInfoHolder hardwareInfo)
	{
		_hardwareInfo = hardwareInfo;
	}

	public ConnectionState getConnectionState() {
		return state;
	}
}
