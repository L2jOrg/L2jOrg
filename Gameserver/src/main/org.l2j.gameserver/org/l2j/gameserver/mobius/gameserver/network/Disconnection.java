package org.l2j.gameserver.mobius.gameserver.network;

import org.l2j.gameserver.mobius.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2j.gameserver.mobius.gameserver.taskmanager.AttackStanceTaskManager;

import java.util.logging.Logger;

/**
 * @author NB4L1
 */
public final class Disconnection
{
	private static final Logger LOGGER = Logger.getLogger(Disconnection.class.getName());
	
	public static L2GameClient getClient(L2GameClient client, L2PcInstance activeChar)
	{
		if (client != null)
		{
			return client;
		}
		
		if (activeChar != null)
		{
			return activeChar.getClient();
		}
		
		return null;
	}
	
	public static L2PcInstance getActiveChar(L2GameClient client, L2PcInstance activeChar)
	{
		if (activeChar != null)
		{
			return activeChar;
		}
		
		if (client != null)
		{
			return client.getActiveChar();
		}
		
		return null;
	}
	
	private final L2GameClient _client;
	private final L2PcInstance _activeChar;
	
	private Disconnection(L2GameClient client)
	{
		this(client, null);
	}
	
	public static Disconnection of(L2GameClient client)
	{
		return new Disconnection(client);
	}
	
	private Disconnection(L2PcInstance activeChar)
	{
		this(null, activeChar);
	}
	
	public static Disconnection of(L2PcInstance activeChar)
	{
		return new Disconnection(activeChar);
	}
	
	private Disconnection(L2GameClient client, L2PcInstance activeChar)
	{
		_client = getClient(client, activeChar);
		_activeChar = getActiveChar(client, activeChar);
		
		// Anti Feed
		AntiFeedManager.getInstance().onDisconnect(_client);
		
		if (_client != null)
		{
			_client.setActiveChar(null);
		}
		
		if (_activeChar != null)
		{
			_activeChar.setClient(null);
		}
	}
	
	public static Disconnection of(L2GameClient client, L2PcInstance activeChar)
	{
		return new Disconnection(client, activeChar);
	}
	
	public Disconnection storeMe()
	{
		try
		{
			if ((_activeChar != null) && _activeChar.isOnline())
			{
				_activeChar.storeMe();
			}
		}
		catch (RuntimeException e)
		{
			LOGGER.warning(e.getMessage());
		}
		
		return this;
	}
	
	public Disconnection deleteMe()
	{
		try
		{
			if ((_activeChar != null) && _activeChar.isOnline())
			{
				EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLogout(_activeChar), _activeChar);
				_activeChar.deleteMe();
			}
		}
		catch (RuntimeException e)
		{
			LOGGER.warning(e.getMessage());
		}
		
		return this;
	}
	
	public Disconnection close(boolean toLoginScreen)
	{
		if (_client != null)
		{
			_client.close(toLoginScreen);
		}
		
		return this;
	}
	
	public Disconnection close(IClientOutgoingPacket packet)
	{
		if (_client != null)
		{
			_client.close(packet);
		}
		
		return this;
	}
	
	public void defaultSequence(boolean toLoginScreen)
	{
		defaultSequence();
		close(toLoginScreen);
	}
	
	public void defaultSequence(IClientOutgoingPacket packet)
	{
		defaultSequence();
		close(packet);
	}
	
	private void defaultSequence()
	{
		storeMe();
		deleteMe();
	}
	
	public void onDisconnection()
	{
		if (_activeChar != null)
		{
			ThreadPool.schedule(() -> defaultSequence(), _activeChar.canLogout() ? 0 : AttackStanceTaskManager.COMBAT_TIME);
		}
	}
}