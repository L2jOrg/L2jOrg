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
package org.l2j.gameserver.mobius.gameserver.model.entity;

import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.commons.database.DatabaseFactory;
import com.l2jmobius.gameserver.data.sql.impl.ClanTable;
import com.l2jmobius.gameserver.data.xml.impl.ClanHallData;
import com.l2jmobius.gameserver.enums.ClanHallGrade;
import com.l2jmobius.gameserver.enums.ClanHallType;
import com.l2jmobius.gameserver.instancemanager.ZoneManager;
import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jmobius.gameserver.model.holders.ClanHallTeleportHolder;
import com.l2jmobius.gameserver.model.itemcontainer.Inventory;
import com.l2jmobius.gameserver.model.residences.AbstractResidence;
import com.l2jmobius.gameserver.model.zone.type.L2ClanHallZone;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author St3eT
 */
public final class ClanHall extends AbstractResidence
{
	// Static parameters
	private final ClanHallGrade _grade;
	private final ClanHallType _type;
	private final int _minBid;
	final int _lease;
	private final int _deposit;
	private final List<Integer> _npcs;
	private final List<L2DoorInstance> _doors;
	private final List<ClanHallTeleportHolder> _teleports;
	private final Location _ownerLocation;
	private final Location _banishLocation;
	// Dynamic parameters
	L2Clan _owner = null;
	long _paidUntil = 0;
	protected ScheduledFuture<?> _checkPaymentTask = null;
	// Other
	private static final String INSERT_CLANHALL = "INSERT INTO clanhall (id, ownerId, paidUntil) VALUES (?,?,?)";
	private static final String LOAD_CLANHALL = "SELECT * FROM clanhall WHERE id=?";
	private static final String UPDATE_CLANHALL = "UPDATE clanhall SET ownerId=?,paidUntil=? WHERE id=?";
	private static final Logger LOGGER = Logger.getLogger(ClanHallData.class.getName());
	
	public ClanHall(StatsSet params)
	{
		super(params.getInt("id"));
		// Set static parameters
		setName(params.getString("name"));
		_grade = params.getEnum("grade", ClanHallGrade.class);
		_type = params.getEnum("type", ClanHallType.class);
		_minBid = params.getInt("minBid");
		_lease = params.getInt("lease");
		_deposit = params.getInt("deposit");
		_npcs = params.getList("npcList", Integer.class);
		_doors = params.getList("doorList", L2DoorInstance.class);
		_teleports = params.getList("teleportList", ClanHallTeleportHolder.class);
		_ownerLocation = params.getLocation("owner_loc");
		_banishLocation = params.getLocation("banish_loc");
		// Set dynamic parameters (from DB)
		load();
		// Init Clan Hall zone and Functions
		initResidenceZone();
		initFunctions();
	}
	
	@Override
	protected void load()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement loadStatement = con.prepareStatement(LOAD_CLANHALL);
			PreparedStatement insertStatement = con.prepareStatement(INSERT_CLANHALL))
		{
			loadStatement.setInt(1, getResidenceId());
			
			try (ResultSet rset = loadStatement.executeQuery())
			{
				if (rset.next())
				{
					setPaidUntil(rset.getLong("paidUntil"));
					setOwner(rset.getInt("ownerId"));
				}
				else
				{
					insertStatement.setInt(1, getResidenceId());
					insertStatement.setInt(2, 0); // New clanhall should not have owner
					insertStatement.setInt(3, 0); // New clanhall should not have paid until
					if (insertStatement.execute())
					{
						LOGGER.info("Clan Hall " + getName() + " (" + getResidenceId() + ") was sucessfully created.");
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.INFO, "Failed loading clan hall", e);
		}
	}
	
	public void updateDB()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_CLANHALL))
		{
			statement.setInt(1, getOwnerId());
			statement.setLong(2, _paidUntil);
			statement.setInt(3, getResidenceId());
			statement.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected void initResidenceZone()
	{
		final L2ClanHallZone zone = ZoneManager.getInstance().getAllZones(L2ClanHallZone.class).stream().filter(z -> z.getResidenceId() == getResidenceId()).findFirst().orElse(null);
		if (zone != null)
		{
			setResidenceZone(zone);
		}
	}
	
	public int getCostFailDay()
	{
		final Duration failDay = Duration.between(Instant.ofEpochMilli(_paidUntil), Instant.now());
		return failDay.isNegative() ? 0 : (int) failDay.toDays();
	}
	
	/**
	 * Teleport all non-owner players from {@link L2ClanHallZone} to {@link ClanHall#getBanishLocation()}.
	 */
	public void banishOthers()
	{
		getResidenceZone().banishForeigners(getOwnerId());
	}
	
	/**
	 * Open or close all {@link L2DoorInstance} related to this {@link ClanHall}.
	 * @param open {@code true} means open door, {@code false} means close door
	 */
	public void openCloseDoors(boolean open)
	{
		_doors.forEach(door -> door.openCloseMe(open));
	}
	
	/**
	 * Gets the grade of clan hall.
	 * @return grade of this {@link ClanHall} in {@link ClanHallGrade} enum.
	 */
	public ClanHallGrade getGrade()
	{
		return _grade;
	}
	
	/**
	 * Gets all {@link L2DoorInstance} related to this {@link ClanHall}.
	 * @return all {@link L2DoorInstance} related to this {@link ClanHall}
	 */
	public List<L2DoorInstance> getDoors()
	{
		return _doors;
	}
	
	/**
	 * Gets all {@link L2Npc} related to this {@link ClanHall}.
	 * @return all {@link L2Npc} related to this {@link ClanHall}
	 */
	public List<Integer> getNpcs()
	{
		return _npcs;
	}
	
	/**
	 * Gets the {@link ClanHallType} of this {@link ClanHall}.
	 * @return {@link ClanHallType} of this {@link ClanHall} in {@link ClanHallGrade} enum.
	 */
	public ClanHallType getType()
	{
		return _type;
	}
	
	/**
	 * Gets the {@link L2Clan} which own this {@link ClanHall}.
	 * @return {@link L2Clan} which own this {@link ClanHall}
	 */
	public L2Clan getOwner()
	{
		return _owner;
	}
	
	/**
	 * Gets the {@link L2Clan} ID which own this {@link ClanHall}.
	 * @return the {@link L2Clan} ID which own this {@link ClanHall}
	 */
	@Override
	public int getOwnerId()
	{
		final L2Clan owner = _owner;
		return (owner != null) ? owner.getId() : 0;
	}
	
	/**
	 * Set the owner of clan hall
	 * @param clanId the Id of the clan
	 */
	public void setOwner(int clanId)
	{
		setOwner(ClanTable.getInstance().getClan(clanId));
	}
	
	/**
	 * Set the clan as owner of clan hall
	 * @param clan the L2Clan object
	 */
	public void setOwner(L2Clan clan)
	{
		if (clan != null)
		{
			_owner = clan;
			clan.setHideoutId(getResidenceId());
			clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
			if (_paidUntil == 0)
			{
				setPaidUntil(Instant.now().plus(Duration.ofDays(7)).toEpochMilli());
			}
			
			final int failDays = getCostFailDay();
			final long time = failDays > 0 ? (failDays > 8 ? Instant.now().toEpochMilli() : Instant.ofEpochMilli(_paidUntil).plus(Duration.ofDays(failDays + 1)).toEpochMilli()) : _paidUntil;
			_checkPaymentTask = ThreadPool.schedule(new CheckPaymentTask(), time - System.currentTimeMillis());
		}
		else
		{
			if (_owner != null)
			{
				_owner.setHideoutId(0);
				_owner.broadcastToOnlineMembers(new PledgeShowInfoUpdate(_owner));
				removeFunctions();
			}
			_owner = null;
			setPaidUntil(0);
			if (_checkPaymentTask != null)
			{
				_checkPaymentTask.cancel(true);
				_checkPaymentTask = null;
			}
		}
		updateDB();
	}
	
	/**
	 * Gets the due date of clan hall payment
	 * @return the due date of clan hall payment
	 */
	public long getPaidUntil()
	{
		return _paidUntil;
	}
	
	/**
	 * Set the due date of clan hall payment
	 * @param paidUntil the due date of clan hall payment
	 */
	public void setPaidUntil(long paidUntil)
	{
		_paidUntil = paidUntil;
	}
	
	/**
	 * Gets the next date of clan hall payment
	 * @return the next date of clan hall payment
	 */
	public long getNextPayment()
	{
		return (_checkPaymentTask != null) ? System.currentTimeMillis() + _checkPaymentTask.getDelay(TimeUnit.MILLISECONDS) : 0;
	}
	
	public Location getOwnerLocation()
	{
		return _ownerLocation;
	}
	
	public Location getBanishLocation()
	{
		return _banishLocation;
	}
	
	public List<ClanHallTeleportHolder> getTeleportList()
	{
		return _teleports;
	}
	
	public List<ClanHallTeleportHolder> getTeleportList(int functionLevel)
	{
		return _teleports.stream().filter(holder -> holder.getMinFunctionLevel() <= functionLevel).collect(Collectors.toList());
	}
	
	public int getMinBid()
	{
		return _minBid;
	}
	
	public int getLease()
	{
		return _lease;
	}
	
	public int getDeposit()
	{
		return _deposit;
	}
	
	class CheckPaymentTask implements Runnable
	{
		@Override
		public void run()
		{
			if (_owner != null)
			{
				if (_owner.getWarehouse().getAdena() < _lease)
				{
					if (getCostFailDay() > 8)
					{
						setOwner(null);
						_owner.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED));
					}
					else
					{
						_checkPaymentTask = ThreadPool.schedule(new CheckPaymentTask(), 24 * 60 * 60 * 1000); // 1 day
						final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
						sm.addInt(_lease);
						_owner.broadcastToOnlineMembers(sm);
					}
				}
				else
				{
					_owner.getWarehouse().destroyItem("Clan Hall Lease", Inventory.ADENA_ID, _lease, null, null);
					setPaidUntil(Instant.ofEpochMilli(_paidUntil).plus(Duration.ofDays(7)).toEpochMilli());
					_checkPaymentTask = ThreadPool.schedule(new CheckPaymentTask(), _paidUntil - System.currentTimeMillis());
					updateDB();
				}
			}
		}
	}
	
	@Override
	public String toString()
	{
		return (getClass().getSimpleName() + ":" + getName() + "[" + getResidenceId() + "]");
	}
}