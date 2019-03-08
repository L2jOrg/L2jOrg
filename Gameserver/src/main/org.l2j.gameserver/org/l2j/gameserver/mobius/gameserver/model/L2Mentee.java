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
package org.l2j.gameserver.mobius.gameserver.model;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author UnAfraid
 */
public class L2Mentee
{
	private static final Logger LOGGER = Logger.getLogger(L2Mentee.class.getName());
	
	private final int _objectId;
	private String _name;
	private int _classId;
	private int _currentLevel;
	
	public L2Mentee(int objectId)
	{
		_objectId = objectId;
		load();
	}
	
	public void load()
	{
		final L2PcInstance player = getPlayerInstance();
		if (player == null) // Only if player is offline
		{
			try (Connection con = DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("SELECT char_name, level, base_class FROM characters WHERE charId = ?"))
			{
				statement.setInt(1, _objectId);
				try (ResultSet rset = statement.executeQuery())
				{
					if (rset.next())
					{
						_name = rset.getString("char_name");
						_classId = rset.getInt("base_class");
						_currentLevel = rset.getInt("level");
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, e.getMessage(), e);
			}
		}
		else
		{
			_name = player.getName();
			_classId = player.getBaseClass();
			_currentLevel = player.getLevel();
		}
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getClassId()
	{
		if (isOnline())
		{
			if (getPlayerInstance().getClassId().getId() != _classId)
			{
				_classId = getPlayerInstance().getClassId().getId();
			}
		}
		return _classId;
	}
	
	public int getLevel()
	{
		if (isOnline())
		{
			if (getPlayerInstance().getLevel() != _currentLevel)
			{
				_currentLevel = getPlayerInstance().getLevel();
			}
		}
		return _currentLevel;
	}
	
	public L2PcInstance getPlayerInstance()
	{
		return L2World.getInstance().getPlayer(_objectId);
	}
	
	public boolean isOnline()
	{
		return (getPlayerInstance() != null) && (getPlayerInstance().isOnlineInt() > 0);
	}
	
	public int isOnlineInt()
	{
		return isOnline() ? getPlayerInstance().isOnlineInt() : 0;
	}
	
	public void sendPacket(IClientOutgoingPacket packet)
	{
		if (isOnline())
		{
			getPlayerInstance().sendPacket(packet);
		}
	}
}
