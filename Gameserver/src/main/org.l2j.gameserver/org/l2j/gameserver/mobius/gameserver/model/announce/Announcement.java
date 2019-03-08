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
package org.l2j.gameserver.mobius.gameserver.model.announce;

import org.l2j.commons.database.DatabaseFactory;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author UnAfraid
 */
public class Announcement implements IAnnouncement
{
	protected static final Logger LOGGER = Logger.getLogger(Announcement.class.getName());
	
	private static final String INSERT_QUERY = "INSERT INTO announcements (type, content, author) VALUES (?, ?, ?)";
	private static final String UPDATE_QUERY = "UPDATE announcements SET type = ?, content = ?, author = ? WHERE id = ?";
	private static final String DELETE_QUERY = "DELETE FROM announcements WHERE id = ?";
	
	protected int _id;
	private AnnouncementType _type;
	private String _content;
	private String _author;
	
	public Announcement(AnnouncementType type, String content, String author)
	{
		_type = type;
		_content = content;
		_author = author;
	}
	
	public Announcement(ResultSet rset) throws SQLException
	{
		_id = rset.getInt("id");
		_type = AnnouncementType.findById(rset.getInt("type"));
		_content = rset.getString("content");
		_author = rset.getString("author");
	}
	
	@Override
	public int getId()
	{
		return _id;
	}
	
	@Override
	public AnnouncementType getType()
	{
		return _type;
	}
	
	@Override
	public void setType(AnnouncementType type)
	{
		_type = type;
	}
	
	@Override
	public String getContent()
	{
		return _content;
	}
	
	@Override
	public void setContent(String content)
	{
		_content = content;
	}
	
	@Override
	public String getAuthor()
	{
		return _author;
	}
	
	@Override
	public void setAuthor(String author)
	{
		_author = author;
	}
	
	@Override
	public boolean isValid()
	{
		return true;
	}
	
	@Override
	public boolean storeMe()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS))
		{
			ps.setInt(1, _type.ordinal());
			ps.setString(2, _content);
			ps.setString(3, _author);
			ps.execute();
			try (ResultSet rset = ps.getGeneratedKeys())
			{
				if (rset.next())
				{
					_id = rset.getInt(1);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't store announcement: ", e);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean updateMe()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_QUERY))
		{
			ps.setInt(1, _type.ordinal());
			ps.setString(2, _content);
			ps.setString(3, _author);
			ps.setInt(4, _id);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't store announcement: ", e);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean deleteMe()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_QUERY))
		{
			ps.setInt(1, _id);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't remove announcement: ", e);
			return false;
		}
		return true;
	}
}
