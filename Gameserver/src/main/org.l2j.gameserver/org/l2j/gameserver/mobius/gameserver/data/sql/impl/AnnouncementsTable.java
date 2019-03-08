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
package org.l2j.gameserver.mobius.gameserver.data.sql.impl;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.mobius.gameserver.enums.ChatType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.announce.Announcement;
import org.l2j.gameserver.mobius.gameserver.model.announce.AnnouncementType;
import org.l2j.gameserver.mobius.gameserver.model.announce.AutoAnnouncement;
import org.l2j.gameserver.mobius.gameserver.model.announce.IAnnouncement;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.CreatureSay;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads announcements from database.
 * @author UnAfraid
 */
public final class AnnouncementsTable
{
	private static Logger LOGGER = Logger.getLogger(AnnouncementsTable.class.getName());
	
	private final Map<Integer, IAnnouncement> _announcements = new ConcurrentSkipListMap<>();
	
	protected AnnouncementsTable()
	{
		load();
	}
	
	private void load()
	{
		_announcements.clear();
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			Statement st = con.createStatement();
			ResultSet rset = st.executeQuery("SELECT * FROM announcements"))
		{
			while (rset.next())
			{
				final AnnouncementType type = AnnouncementType.findById(rset.getInt("type"));
				final Announcement announce;
				switch (type)
				{
					case NORMAL:
					case CRITICAL:
					{
						announce = new Announcement(rset);
						break;
					}
					case AUTO_NORMAL:
					case AUTO_CRITICAL:
					{
						announce = new AutoAnnouncement(rset);
						break;
					}
					default:
					{
						continue;
					}
				}
				_announcements.put(announce.getId(), announce);
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Failed loading announcements:", e);
		}
	}
	
	/**
	 * Sending all announcements to the player
	 * @param player
	 */
	public void showAnnouncements(L2PcInstance player)
	{
		sendAnnouncements(player, AnnouncementType.NORMAL);
		sendAnnouncements(player, AnnouncementType.CRITICAL);
		sendAnnouncements(player, AnnouncementType.EVENT);
	}
	
	/**
	 * Sends all announcements to the player by the specified type
	 * @param player
	 * @param type
	 */
	private void sendAnnouncements(L2PcInstance player, AnnouncementType type)
	{
		for (IAnnouncement announce : _announcements.values())
		{
			if (announce.isValid() && (announce.getType() == type))
			{
				player.sendPacket(new CreatureSay(0, //
					type == AnnouncementType.CRITICAL ? ChatType.CRITICAL_ANNOUNCE : ChatType.ANNOUNCEMENT, //
					player.getName(), announce.getContent()));
			}
		}
	}
	
	/**
	 * Adds announcement
	 * @param announce
	 */
	public void addAnnouncement(IAnnouncement announce)
	{
		if (announce.storeMe())
		{
			_announcements.put(announce.getId(), announce);
		}
	}
	
	/**
	 * Removes announcement by id
	 * @param id
	 * @return {@code true} if announcement exists and was deleted successfully, {@code false} otherwise.
	 */
	public boolean deleteAnnouncement(int id)
	{
		final IAnnouncement announce = _announcements.remove(id);
		return (announce != null) && announce.deleteMe();
	}
	
	/**
	 * @param id
	 * @return {@link IAnnouncement} by id
	 */
	public IAnnouncement getAnnounce(int id)
	{
		return _announcements.get(id);
	}
	
	/**
	 * @return {@link Collection} containing all announcements
	 */
	public Collection<IAnnouncement> getAllAnnouncements()
	{
		return _announcements.values();
	}
	
	/**
	 * @return Single instance of {@link AnnouncementsTable}
	 */
	public static AnnouncementsTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final AnnouncementsTable _instance = new AnnouncementsTable();
	}
}
