package org.l2j.gameserver.model.entity.residence;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import io.github.joealisson.primitive.sets.IntSet;
import io.github.joealisson.primitive.sets.impl.HashIntSet;
import org.l2j.commons.dao.JdbcEntity;
import org.l2j.commons.dao.JdbcEntityState;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.dbutils.DbUtils;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Contants.Items;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.data.xml.holder.EventHolder;
import org.l2j.gameserver.data.xml.holder.ResidenceFunctionsHolder;
import org.l2j.gameserver.instancemanager.ReflectionManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.Zone;
import org.l2j.gameserver.model.base.ResidenceFunctionType;
import org.l2j.gameserver.model.entity.Reflection;
import org.l2j.gameserver.model.entity.events.EventType;
import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.templates.StatsSet;
import org.l2j.gameserver.templates.residence.ResidenceFunctionTemplate;
import org.l2j.gameserver.utils.Location;
import org.l2j.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @reworked VISTALL
 */
public abstract class Residence implements JdbcEntity
{
	public class ResidenceCycleTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			chanceCycle();

			update();
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(Residence.class);
	public static final long CYCLE_TIME = 60 * 60 * 1000L; // 1 час

	private final int _id;
	private final String _name;

	protected Clan _owner;
	protected Zone _zone;

	private IntObjectMap<Skill> _skills = new HashIntObjectMap<Skill>();

	protected SiegeEvent<?, ?> _siegeEvent;

	protected Calendar _siegeDate = Calendar.getInstance();
	protected Calendar _lastSiegeDate = Calendar.getInstance();
	protected Calendar _ownDate = Calendar.getInstance();

	// rewards
	protected ScheduledFuture<?> _cycleTask;
	private int _cycle;
	private int _paidCycle;

	protected JdbcEntityState _jdbcEntityState = JdbcEntityState.CREATED;

	// points
	protected List<Location> _banishPoints = new ArrayList<Location>();
	protected List<Location> _ownerRestartPoints = new ArrayList<Location>();
	protected List<Location> _otherRestartPoints = new ArrayList<Location>();
	protected List<Location> _chaosRestartPoints = new ArrayList<Location>();

	private final Map<ResidenceFunctionType, ResidenceFunction> _activeFunctions = new HashMap<ResidenceFunctionType, ResidenceFunction>();

	private final IntSet _availableFunctions = new HashIntSet();

	public Residence(StatsSet set)
	{
		_id = set.getInteger("id");
		_name = set.getString("name");

		_siegeDate.setTimeInMillis(0);
		_lastSiegeDate.setTimeInMillis(0);
		_ownDate.setTimeInMillis(0);

		initZone();
	}

	public abstract ResidenceType getType();

	public void init()
	{
		initEvent();

		loadData();
		loadFunctions();
		rewardSkills();
		startCycleTask();
	}

	protected void initZone()
	{
		_zone = ReflectionUtils.getZone("residence_" + getId());
		_zone.setParam("residence", this);
	}

	protected void initEvent()
	{
		_siegeEvent = EventHolder.getInstance().getEvent(EventType.SIEGE_EVENT, getId());
	}

	@SuppressWarnings("unchecked")
	public <E extends SiegeEvent<?, ?>> E getSiegeEvent()
	{
		return (E) _siegeEvent;
	}

	public int getId()
	{
		return _id;
	}

	public String getName()
	{
		return _name;
	}

	public int getOwnerId()
	{
		return _owner == null ? 0 : _owner.getClanId();
	}

	public void setOwner(Clan owner)
	{
		_owner = owner;
	}

	public Clan getOwner()
	{
		return _owner;
	}

	public boolean isOwner(int clanId)
	{
		return _owner == null ? false : _owner.getClanId() == clanId;
	}

	public Zone getZone()
	{
		return _zone;
	}

	public boolean isInstant()
	{
		return false;
	}

	protected abstract void loadData();

	public abstract void changeOwner(Clan clan);

	public Calendar getOwnDate()
	{
		return _ownDate;
	}

	public Calendar getSiegeDate()
	{
		return _siegeDate;
	}

	public Calendar getLastSiegeDate()
	{
		return _lastSiegeDate;
	}

	public void addAvailableFunction(int id)
	{
		_availableFunctions.add(id);
	}

	public void addSkill(Skill skill)
	{
		_skills.put(skill.getId(), skill);
	}

	public void removeSkill(Skill skill)
	{
		_skills.remove(skill.getId());
	}

	public boolean checkIfInZone(Location loc, Reflection ref)
	{
		return checkIfInZone(loc.x, loc.y, loc.z, ref);
	}

	public boolean checkIfInZone(int x, int y, int z, Reflection ref)
	{
		return getZone() != null && getZone().checkIfInZone(x, y, z, ref);
	}

	public void banishForeigner(int clanId)
	{
		for(Player player : _zone.getInsidePlayers())
		{
			if(player.getClanId() == getOwnerId())
				continue;

			player.teleToLocation(getBanishPoint());
		}
	}

	/**
	 * Выдает клану-владельцу скилы резиденции
	 */
	public void rewardSkills()
	{
		Clan owner = getOwner();
		if(owner != null)
		{
			for(Skill skill : getSkills())
			{
				if(owner.addSkill(skill.getEntry(), false) == null)
					owner.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));
			}
		}
	}

	/**
	 * Удаляет у клана-владельца скилы резиденции
	 */
	public void removeSkills()
	{
		Clan owner = getOwner();
		if(owner != null)
		{
			for(Skill skill : getSkills())
				owner.removeSkill(skill.getId());
		}
	}

	protected void loadFunctions()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM residence_functions WHERE residence_id=?");
			statement.setInt(1, getId());
			rs = statement.executeQuery();
			while(rs.next())
			{
				ResidenceFunctionType type = ResidenceFunctionType.VALUES[rs.getInt("type")];

				ResidenceFunctionTemplate functionTemplate = ResidenceFunctionsHolder.getInstance().getTemplate(type, rs.getInt("level"));
				if (functionTemplate == null || !isFunctionAvailable(functionTemplate))
					removeFunction(type);
				else
				{
					ResidenceFunction function = new ResidenceFunction(functionTemplate, getId());
					function.setEndTimeInMillis(rs.getInt("end_time") * 1000L);
					function.setInDebt(rs.getBoolean("in_debt"));

					addActiveFunction(function);

					startAutoTaskForFunction(function);
				}
			}
		}
		catch(Exception e)
		{
			_log.warn("Residence: loadFunctions(): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rs);
		}
	}

	public void addActiveFunction(ResidenceFunction function)
	{
		_activeFunctions.put(function.getType(), function);
	}

	public boolean isFunctionActive(ResidenceFunctionType type)
	{
		return _activeFunctions.containsKey(type);
	}

	public ResidenceFunction getActiveFunction(ResidenceFunctionType type)
	{
		return _activeFunctions.get(type);
	}

	public boolean updateFunctions(ResidenceFunctionType type, int level)
	{
		Clan clan = getOwner();
		if(clan == null)
			return false;

		ResidenceFunction activeFunction = getActiveFunction(type);
		if(activeFunction != null && activeFunction.getLevel() == level)
			return true;

		if(level == 0)
		{
			if(activeFunction != null)
			{
				removeFunction(type);
				return true;
			}

			return false;
		}

		ResidenceFunctionTemplate functionTemplate = ResidenceFunctionsHolder.getInstance().getTemplate(type, level);
		if(!isFunctionAvailable(functionTemplate))
			return false;

		long clanAdenaCount = clan.getAdenaCount();
		long lease = functionTemplate.getCost();

		if (activeFunction == null)
		{
			if(clanAdenaCount >= lease)
				clan.getWarehouse().destroyItemByItemId(Items.ADENA, lease);
			else
				return false;
		}
		else
		{
			long activeFunctionLease = activeFunction.getTemplate().getCost() / activeFunction.getTemplate().getPeriod() * functionTemplate.getPeriod();
			if(clanAdenaCount >= lease - activeFunctionLease)
			{
				if(lease > activeFunctionLease)
					clan.getWarehouse().destroyItemByItemId(Items.ADENA, lease - activeFunctionLease);
			}
			else
				return false;
		}

		long time = Calendar.getInstance().getTimeInMillis() + functionTemplate.getPeriod() * 24 * 60 * 60 * 1000L;

		ResidenceFunction function = new ResidenceFunction(functionTemplate, getId());
		function.setEndTimeInMillis(time);

		_activeFunctions.put(function.getType(), function);

		startAutoTaskForFunction(function);

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE residence_functions SET residence_id=?, type=?, level=?, end_time=?");
			statement.setInt(1, getId());
			statement.setInt(2, type.ordinal());
			statement.setInt(3, level);
			statement.setInt(4, (int) (time / 1000));
			statement.execute();

		}
		catch (Exception e)
		{
			_log.warn("Exception: updateFunctions(ResidenceFunctionType,int): " + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);

		}
		return true;
	}

	public void removeFunction(ResidenceFunctionType type)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM residence_functions WHERE residence_id=? AND type=?");
			statement.setInt(1, getId());
			statement.setInt(2, type.ordinal());

			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn("Exception: removeFunction(int type): " + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		_activeFunctions.remove(type);
	}

	public void removeFunctions()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM residence_functions WHERE residence_id=?");
			statement.setInt(1, getId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn("Exception: removeFunctions(): " + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		_activeFunctions.clear();
	}

	private void startAutoTaskForFunction(ResidenceFunction function)
	{
		Clan clan = getOwner();
		if(clan == null)
			return;

		if(function.getEndTimeInMillis() > System.currentTimeMillis())
			ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
		else if(function.isInDebt() && clan.getAdenaCount() >= function.getTemplate().getCost()) // if player didn't pay before add extra fee
		{
			clan.getWarehouse().destroyItemByItemId(Items.ADENA, function.getTemplate().getCost());
			function.updateRentTime(false);
			ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
		}
		else if(!function.isInDebt())
		{
			function.setInDebt(true);
			function.updateRentTime(true);
			ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
		}
		else
			removeFunction(function.getType());
	}

	private class AutoTaskForFunctions extends RunnableImpl
	{
		ResidenceFunction _function;

		public AutoTaskForFunctions(ResidenceFunction function)
		{
			_function = function;
		}

		@Override
		public void runImpl() throws Exception
		{
			startAutoTaskForFunction(_function);
		}
	}

	@Override
	public void setJdbcState(JdbcEntityState state)
	{
		_jdbcEntityState = state;
	}

	@Override
	public JdbcEntityState getJdbcState()
	{
		return _jdbcEntityState;
	}

	@Override
	public void save()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete()
	{
		throw new UnsupportedOperationException();
	}

	public void cancelCycleTask()
	{
		_cycle = 0;
		_paidCycle = 0;
		if(_cycleTask != null)
		{
			_cycleTask.cancel(false);
			_cycleTask = null;
		}

		setJdbcState(JdbcEntityState.UPDATED);
	}

	public void startCycleTask()
	{
		if(_owner == null)
			return;

		long ownedTime = getOwnDate().getTimeInMillis();
		if(ownedTime == 0)
			return;

		long diff = System.currentTimeMillis() - ownedTime;
		while(diff >= CYCLE_TIME)
			diff -= CYCLE_TIME;

		_cycleTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ResidenceCycleTask(), diff, CYCLE_TIME);
	}

	public void chanceCycle()
	{
		setCycle(getCycle() + 1);
		setJdbcState(JdbcEntityState.UPDATED);
	}

	public List<Skill> getSkills()
	{
		return new ArrayList<Skill>(_skills.values());
	}

	public void addBanishPoint(Location loc)
	{
		_banishPoints.add(loc);
	}

	public void addOwnerRestartPoint(Location loc)
	{
		_ownerRestartPoints.add(loc);
	}

	public void addOtherRestartPoint(Location loc)
	{
		_otherRestartPoints.add(loc);
	}

	public void addChaosRestartPoint(Location loc)
	{
		_chaosRestartPoints.add(loc);
	}

	public Location getBanishPoint()
	{
		if(_banishPoints.isEmpty())
			return null;
		return _banishPoints.get(Rnd.get(_banishPoints.size()));
	}

	public Location getOwnerRestartPoint()
	{
		if(_ownerRestartPoints.isEmpty())
			return null;
		return _ownerRestartPoints.get(Rnd.get(_ownerRestartPoints.size()));
	}

	public Location getOtherRestartPoint()
	{
		if(_otherRestartPoints.isEmpty())
			return null;
		return _otherRestartPoints.get(Rnd.get(_otherRestartPoints.size()));
	}

	public Location getChaosRestartPoint()
	{
		if(_chaosRestartPoints.isEmpty())
			return null;
		return _chaosRestartPoints.get(Rnd.get(_chaosRestartPoints.size()));
	}

	public Location getNotOwnerRestartPoint(Player player)
	{
		return player.isPK() ? getChaosRestartPoint() : getOtherRestartPoint();
	}

	public int getCycle()
	{
		return _cycle;
	}

	public long getCycleDelay()
	{
		if(_cycleTask == null)
			return 0;
		return _cycleTask.getDelay(TimeUnit.SECONDS);
	}

	public void setCycle(int cycle)
	{
		_cycle = cycle;
	}

	public int getPaidCycle()
	{
		return _paidCycle;
	}

	public void setPaidCycle(int paidCycle)
	{
		_paidCycle = paidCycle;
	}

	public void setResidenceSide(ResidenceSide side, boolean onRestore)
	{}

	public ResidenceSide getResidenceSide()
	{
		return ResidenceSide.NEUTRAL;
	}

	public void broadcastResidenceState()
	{}

	public int getVisibleFunctionLevel(int level)
	{
		return level - 10;
	}

	public boolean isFunctionAvailable(ResidenceFunctionTemplate template)
	{
		if (getVisibleFunctionLevel(template.getLevel()) <= 0)
			return false;
		return _availableFunctions.contains(template.getId());
	}

	public List<ResidenceFunctionTemplate> getAvailableFunctions(ResidenceFunctionType type)
	{
		List<ResidenceFunctionTemplate> functions = new ArrayList<ResidenceFunctionTemplate>();
		for(ResidenceFunctionTemplate template : ResidenceFunctionsHolder.getInstance().getTemplates(type))
		{
			if(isFunctionAvailable(template))
				functions.add(template);
		}
		return functions;
	}

	public Reflection getReflection(int clanId)
	{
		return ReflectionManager.MAIN;
	}

	public static int getInstantResidenceId(int instantId)
	{
		return instantId * 1000;
	}
}