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
package org.l2j.gameserver.mobius.gameserver.model.olympiad;

import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author DS, Zoey76
 */
public final class Participant
{
	private final int objectId;
	private L2PcInstance player;
	private final String name;
	private final int side;
	private final int baseClass;
	private boolean disconnected = false;
	private boolean defaulted = false;
	private final StatsSet stats;
	public String clanName;
	public int clanId;
	
	public Participant(L2PcInstance plr, int olympiadSide)
	{
		objectId = plr.getObjectId();
		player = plr;
		name = plr.getName();
		side = olympiadSide;
		baseClass = plr.getBaseClass();
		stats = Olympiad.getNobleStats(objectId);
		clanName = plr.getClan() != null ? plr.getClan().getName() : "";
		clanId = plr.getClanId();
	}
	
	public Participant(int objId, int olympiadSide)
	{
		objectId = objId;
		player = null;
		name = "-";
		side = olympiadSide;
		baseClass = 0;
		stats = null;
		clanName = "";
		clanId = 0;
	}
	
	/**
	 * Updates the reference to {@link #player}, if it's null or appears off-line.
	 * @return {@code true} if after the update the player isn't null, {@code false} otherwise.
	 */
	public final boolean updatePlayer()
	{
		if ((player == null) || !player.isOnline())
		{
			player = L2World.getInstance().getPlayer(getObjectId());
		}
		return (player != null);
	}
	
	/**
	 * @param statName
	 * @param increment
	 */
	public final void updateStat(String statName, int increment)
	{
		stats.set(statName, Math.max(stats.getInt(statName) + increment, 0));
	}
	
	/**
	 * @return the name the player's name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return the name the player's clan name.
	 */
	public String getClanName()
	{
		return clanName;
	}
	
	/**
	 * @return the name the player's id.
	 */
	public int getClanId()
	{
		return clanId;
	}
	
	/**
	 * @return the player
	 */
	public L2PcInstance getPlayer()
	{
		return player;
	}
	
	/**
	 * @return the objectId
	 */
	public int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * @return the stats
	 */
	public StatsSet getStats()
	{
		return stats;
	}
	
	/**
	 * @param noble the player to set
	 */
	public void setPlayer(L2PcInstance noble)
	{
		player = noble;
	}
	
	/**
	 * @return the side
	 */
	public int getSide()
	{
		return side;
	}
	
	/**
	 * @return the baseClass
	 */
	public int getBaseClass()
	{
		return baseClass;
	}
	
	/**
	 * @return the disconnected
	 */
	public boolean isDisconnected()
	{
		return disconnected;
	}
	
	/**
	 * @param val the disconnected to set
	 */
	public void setDisconnected(boolean val)
	{
		disconnected = val;
	}
	
	/**
	 * @return the defaulted
	 */
	public boolean isDefaulted()
	{
		return defaulted;
	}
	
	/**
	 * @param val the value to set.
	 */
	public void setDefaulted(boolean val)
	{
		defaulted = val;
	}
}