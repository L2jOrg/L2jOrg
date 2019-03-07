/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.instancemanager;

import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.commons.database.DatabaseFactory;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.Containers;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenersContainer;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLogin;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLogout;
import com.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Mobius
 */
public class PremiumManager
{
	// SQL Statement
	private static final String LOAD_SQL = "SELECT account_name,enddate FROM account_premium WHERE account_name = ?";
	private static final String UPDATE_SQL = "REPLACE INTO account_premium (enddate,account_name) VALUE (?,?)";
	private static final String DELETE_SQL = "DELETE FROM account_premium WHERE account_name = ?";
	
	class PremiumExpireTask implements Runnable
	{
		final L2PcInstance player;
		
		PremiumExpireTask(L2PcInstance player)
		{
			this.player = player;
		}
		
		@Override
		public void run()
		{
			player.setPremiumStatus(false);
		}
	}
	
	// Data Cache
	private final Map<String, Long> premiumData = new HashMap<>();
	
	// expireTasks
	private final Map<String, ScheduledFuture<?>> expiretasks = new HashMap<>();
	
	// Listeners
	private final ListenersContainer listenerContainer = Containers.Players();
	
	private final Consumer<OnPlayerLogin> playerLoginEvent = (event) ->
	{
		final L2PcInstance player = event.getActiveChar();
		final String accountName = player.getAccountName();
		loadPremiumData(accountName);
		final long now = System.currentTimeMillis();
		final long premiumExpiration = getPremiumExpiration(accountName);
		player.setPremiumStatus(premiumExpiration > now);
		
		if (player.hasPremiumStatus())
		{
			startExpireTask(player, premiumExpiration - now);
		}
		else if (premiumExpiration > 0)
		{
			removePremiumStatus(accountName, false);
		}
	};
	
	private final Consumer<OnPlayerLogout> playerLogoutEvent = (event) ->
	{
		L2PcInstance player = event.getActiveChar();
		stopExpireTask(player);
	};
	
	protected PremiumManager()
	{
		listenerContainer.addListener(new ConsumerEventListener(listenerContainer, EventType.ON_PLAYER_LOGIN, playerLoginEvent, this));
		listenerContainer.addListener(new ConsumerEventListener(listenerContainer, EventType.ON_PLAYER_LOGOUT, playerLogoutEvent, this));
	}
	
	/**
	 * @param player
	 * @param delay
	 */
	private void startExpireTask(L2PcInstance player, long delay)
	{
		ScheduledFuture<?> task = ThreadPool.schedule(new PremiumExpireTask(player), delay);
		expiretasks.put(player.getAccountName(), task);
	}
	
	/**
	 * @param player
	 */
	private void stopExpireTask(L2PcInstance player)
	{
		ScheduledFuture<?> task = expiretasks.remove(player.getAccountName());
		if (task != null)
		{
			task.cancel(false);
			task = null;
		}
	}
	
	private void loadPremiumData(String accountName)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(LOAD_SQL))
		{
			stmt.setString(1, accountName);
			try (ResultSet rset = stmt.executeQuery())
			{
				while (rset.next())
				{
					premiumData.put(rset.getString(1), rset.getLong(2));
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public long getPremiumExpiration(String accountName)
	{
		return premiumData.getOrDefault(accountName, 0L);
	}
	
	public void addPremiumTime(String accountName, int timeValue, TimeUnit timeUnit)
	{
		long addTime = timeUnit.toMillis(timeValue);
		long now = System.currentTimeMillis();
		// new premium task at least from now
		long oldPremiumExpiration = Math.max(now, getPremiumExpiration(accountName));
		long newPremiumExpiration = oldPremiumExpiration + addTime;
		
		// UPDATE DATABASE
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_SQL))
		{
			stmt.setLong(1, newPremiumExpiration);
			stmt.setString(2, accountName);
			stmt.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		// UPDATE CACHE
		premiumData.put(accountName, newPremiumExpiration);
		
		// UPDATE PlAYER PREMIUMSTATUS
		L2PcInstance playerOnline = L2World.getInstance().getPlayers().stream().filter(p -> accountName.equals(p.getAccountName())).findFirst().orElse(null);
		if (playerOnline != null)
		{
			stopExpireTask(playerOnline);
			startExpireTask(playerOnline, newPremiumExpiration - now);
			
			if (!playerOnline.hasPremiumStatus())
			{
				playerOnline.setPremiumStatus(true);
			}
		}
	}
	
	public void removePremiumStatus(String accountName, boolean checkOnline)
	{
		if (checkOnline)
		{
			L2PcInstance playerOnline = L2World.getInstance().getPlayers().stream().filter(p -> accountName.equals(p.getAccountName())).findFirst().orElse(null);
			if ((playerOnline != null) && playerOnline.hasPremiumStatus())
			{
				playerOnline.setPremiumStatus(false);
				stopExpireTask(playerOnline);
			}
		}
		
		// UPDATE CACHE
		premiumData.remove(accountName);
		
		// UPDATE DATABASE
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_SQL))
		{
			stmt.setString(1, accountName);
			stmt.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static PremiumManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PremiumManager _instance = new PremiumManager();
	}
}