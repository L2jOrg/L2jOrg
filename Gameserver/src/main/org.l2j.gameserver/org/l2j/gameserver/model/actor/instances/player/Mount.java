package org.l2j.gameserver.model.actor.instances.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.Future;

import org.apache.commons.lang3.ArrayUtils;
import org.l2j.commons.dbutils.DbUtils;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.data.xml.holder.PetDataHolder;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.database.DatabaseFactory;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.base.MountType;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.RidePacket;
import org.l2j.gameserver.network.l2.s2c.SetupGaugePacket;
import org.l2j.gameserver.templates.pet.PetData;
import org.l2j.gameserver.templates.pet.PetLevelData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 * @date 19.01.2012
 */
public class Mount
{
	private static final Logger _log = LoggerFactory.getLogger(Mount.class);

	private class FeedTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if(isHungry())
				tryFeed();

			if(_currentFeed <= 0)
			{
				_rider.sendPacket(SystemMsg.YOU_ARE_OUT_OF_FEED);
				_rider.setMount(null);
				return;
			}

			consumeMeal();
		}
	}

	private final Player _rider;
	private final int _controlItemObjId;
	private final int _npcId;
	private final int _level;
	private final int _formId;
	private final PetLevelData _data;
	private final MountType _type;

	private int _currentFeed;
	private Future<?> _feedTask;

	public Mount(Player rider, int controlItemObjId, int npcId, int level, int currentFeed, int formId, PetLevelData data, MountType type)
	{
		_rider = rider;
		_controlItemObjId = controlItemObjId;
		_npcId = npcId;
		_level = level;
		_currentFeed = currentFeed;
		_formId = formId;
		_data = data;
		_type = type;
	}

	public int getControlItemObjId()
	{
		return _controlItemObjId;
	}

	public int getNpcId()
	{
		return _npcId;
	}

	public int getLevel()
	{
		return _level;
	}

	public void setCurrentFeed(int val)
	{
		_currentFeed = Math.min(_data.getMaxMeal(), Math.max(0, val));
	}

	public int getCurrentFeed()
	{
		return _currentFeed;
	}

	public int getFormId()
	{
		return _formId;
	}

	public int getBattleMealConsumeOnRide()
	{
		return _data.getBattleMealConsumeOnRide();
	}

	public int getWalkSpdOnRide()
	{
		return _data.getWalkSpdOnRide();
	}

	public int getRunSpdOnRide()
	{
		return _data.getRunSpdOnRide();
	}

	public int getWaterWalkSpdOnRide()
	{
		return _data.getWaterWalkSpdOnRide();
	}

	public int getWaterRunSpdOnRide()
	{
		return _data.getWaterRunSpdOnRide();
	}

	public int getFlyWalkSpdOnRide()
	{
		return _data.getFlyWalkSpdOnRide();
	}

	public int getFlyRunSpdOnRide()
	{
		return _data.getFlyRunSpdOnRide();
	}

	public int getAtkSpdOnRide()
	{
		return _data.getAtkSpdOnRide();
	}

	public double getPAtkOnRide()
	{
		return _data.getPAtkOnRide();
	}

	public double getMAtkOnRide()
	{
		return _data.getMAtkOnRide();
	}

	public int getMaxHpOnRide()
	{
		return _data.getMaxHpOnRide();
	}

	public int getMaxMpOnRide()
	{
		return _data.getMaxMpOnRide();
	}

	public boolean isMyFeed(int itemId)
	{
		return ArrayUtils.contains(_data.getFood(), itemId);
	}

	public MountType getType()
	{
		return _type;
	}

	public boolean isOfType(MountType type)
	{
		return _type == type;
	}

	public void onRide()
	{
		switch(getType())
		{
			case WYVERN:
				_rider.setFlying(true);
				_rider.setLoc(_rider.getLoc().changeZ(32));
				_rider.addSkill(SkillHolder.getInstance().getSkillEntry(Skill.SKILL_WYVERN_BREATH, 1), false);
				_rider.sendSkillList();
				break;
		}

		_rider.unEquipWeapon();
		_rider.broadcastUserInfo(true); // нужно послать пакет перед Ride для корректного снятия оружия с заточкой
		_rider.broadcastPacket(new RidePacket(_rider));
		_rider.broadcastUserInfo(true); // нужно послать пакет после Ride для корректного отображения скорости

		updateStatus();
		startFeedTask();
	}

	public void onUnride()
	{
		onLogout();
		_rider.setFlying(false);

		boolean sendSkillList = false;
		if(_rider.removeSkillById(Skill.SKILL_STRIDER_ASSAULT) != null)
			sendSkillList = true;
		if(_rider.removeSkillById(Skill.SKILL_WYVERN_BREATH) != null)
			sendSkillList = true;
		if(sendSkillList)
			_rider.sendSkillList();

        _rider.getAbnormalList().stop(Skill.SKILL_HINDER_STRIDER);
		_rider.broadcastUserInfo(true); // нужно послать пакет перед Ride для корректного снятия оружия с заточкой
		_rider.broadcastPacket(new RidePacket(_rider));
		_rider.broadcastUserInfo(true); // нужно послать пакет после Ride для корректного отображения скорости
		_rider.sendPacket(new SetupGaugePacket(_rider, SetupGaugePacket.Colors.GREEN, 0));
	}

	public void onLogout()
	{
		stopFeedTask();
		store();
	}

	public void onDeath()
	{
		stopFeedTask();
	}

	public void onRevive()
	{
		startFeedTask();
	}

	public void onControlItemDelete()
	{
		_rider.setMount(null);
	}

	private void startFeedTask()
	{
		if(_currentFeed == -1)
			return;

		if(_feedTask != null)
			return;

		_feedTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FeedTask(), 10000L, 10000L);
	}

	private void stopFeedTask()
	{
		if(_feedTask != null)
		{
			_feedTask.cancel(false);
			_feedTask = null;
		}
	}

	private void consumeMeal()
	{
		_currentFeed -= _rider.isInCombat() ? _data.getBattleMealConsumeOnRide() : _data.getNormalMealConsumeOnRide();
		if(_currentFeed < 0)
			_currentFeed = 0;
		updateStatus();
	}

	public void updateStatus()
	{
		int mealConsume = _rider.isInCombat() ? _data.getBattleMealConsumeOnRide() : _data.getNormalMealConsumeOnRide();
		int time = _data.getMaxMeal() / mealConsume * 60000;
		int timeLost = _currentFeed / mealConsume * 60000;
		_rider.sendPacket(new SetupGaugePacket(_rider, SetupGaugePacket.Colors.GREEN, time, timeLost));
		_rider.sendUserInfo(false);
	}

	public boolean isHungry()
	{
		if(_controlItemObjId == 0)
			return false;

		return _currentFeed < (int) (_data.getMaxMeal() * 0.01 * _data.getHungryLimit());
	}

	private void tryFeed()
	{
		ItemInstance food = null;
		for(int foodId : _data.getFood())
		{
			food = _rider.getInventory().getItemByItemId(foodId);
			if(food != null)
			{
				if(_rider.useItem(food, false, false))
					break;
				food = null;
			}
		}
	}

	public void store()
	{
		if(_controlItemObjId == 0)
			return;

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			String req = "UPDATE pets SET fed=? WHERE item_obj_id = ?";
			statement = con.prepareStatement(req);
			statement.setInt(1, _currentFeed);
			statement.setInt(2, _controlItemObjId);
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.error("Could not store mount current feed!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public static Mount create(Player rider, int controlItemObjId, int npcId, int level, int currentFeed)
	{
		if(rider == null)
			return null;

		PetData template = PetDataHolder.getInstance().getTemplateByNpcId(npcId);
		if(template == null)
			return null;

		PetLevelData data = template.getLvlData(level);
		if(data == null)
			return null;

		return new Mount(rider, controlItemObjId, npcId, level, currentFeed, template.getFormId(level), data, template.getMountType());
	}
}