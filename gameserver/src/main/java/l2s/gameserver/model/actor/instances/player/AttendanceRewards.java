package l2s.gameserver.model.actor.instances.player;

import java.util.Collection;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.dao.AccountVariablesDAO;
import l2s.gameserver.data.xml.holder.AttendanceRewardHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExConfirmVipAttendanceCheck;
import l2s.gameserver.network.l2.s2c.ExVipAttendanceItemList;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.data.AttendanceRewardData;
import l2s.gameserver.utils.ItemFunctions;

public class AttendanceRewards
{
	private static final SchedulingPattern VIP_ATTENDANCE_DATE_PATTERN = new SchedulingPattern("30 6 * * *");
	private static final String VIP_ATTENDANCE_REWARD_INDEX_VAR = "@vip_attendance_reward_index";
	private static final String VIP_ATTENDANCE_REWARD_DATE_VAR = "@vip_attendance_date_index";
	private static final int AFTER_LOGIN_RECEIVE_REWARD_DELAY = 30;
	protected final ReadWriteLock lock = new ReentrantReadWriteLock();
	protected final Lock readLock = lock.readLock();
	protected final Lock writeLock = lock.writeLock();
	private final Player _owner;
	private int _receivedRewardIndex = 0;
	private int _nextRewardIndex = 0;
	private ScheduledFuture<?> _loginDelayTask = null;
	private ScheduledFuture<?> _getNextRewardIndexTask = null;

	public AttendanceRewards(Player owner)
	{
		_owner = owner;
	}

	public void restore()
	{
		writeLock();
		try
		{
			if(!Config.VIP_ATTENDANCE_REWARDS_ENABLED)
				return;

			Collection<AttendanceRewardData> rewards = AttendanceRewardHolder.getInstance().getRewards(_owner.hasPremiumAccount());
			if(rewards.isEmpty())
				return;

			int receiveTime;
			if(Config.VIP_ATTENDANCE_REWARDS_REWARD_BY_ACCOUNT)
				receiveTime = Integer.parseInt(AccountVariablesDAO.getInstance().select(_owner.getAccountName(), VIP_ATTENDANCE_REWARD_DATE_VAR, "0"));
			else
				receiveTime = _owner.getVarInt(VIP_ATTENDANCE_REWARD_DATE_VAR, 0);

			int index = 0;
			if(Config.VIP_ATTENDANCE_REWARDS_REWARD_BY_ACCOUNT)
				index = Integer.parseInt(AccountVariablesDAO.getInstance().select(_owner.getAccountName(), VIP_ATTENDANCE_REWARD_INDEX_VAR, "0"));
			else
				index = _owner.getVarInt(VIP_ATTENDANCE_REWARD_INDEX_VAR, 0);

			index = Math.max(0, Math.min(index, rewards.size()));
			_receivedRewardIndex = index;
			long nextRewardTime = AttendanceRewards.VIP_ATTENDANCE_DATE_PATTERN.next(receiveTime * 1000L);
			if(nextRewardTime <= System.currentTimeMillis())
			{
				if(index >= rewards.size())
				{
					_receivedRewardIndex = 0;
					index = 1;
				}
				else
					index++;
			}
			_nextRewardIndex = index;
		}
		finally
		{
			writeUnlock();
		}
	}

	public int getReceivedRewardIndex()
	{
		readLock();
		try
		{
			return _receivedRewardIndex;
		}
		finally
		{
			readUnlock();
		}
	}

	public int getNextRewardIndex()
	{
		readLock();
		try
		{
			return _nextRewardIndex;
		}
		finally
		{
			readUnlock();
		}
	}

	public boolean isAvailable()
	{
		return getNextRewardIndex() > 0;
	}

	public boolean isReceived()
	{
		return getReceivedRewardIndex() == getNextRewardIndex();
	}

	public boolean receiveReward()
	{
		writeLock();
		try
		{
			if(!isAvailable())
				return false;

			if(isReceived())
				return false;

			AttendanceRewardData reward = AttendanceRewardHolder.getInstance().getReward(getNextRewardIndex(), _owner.hasPremiumAccount());
			if(reward == null)
			{
				_owner.sendPacket(SystemMsg.DUE_TO_A_SYSTEM_ERROR_THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_PLEASE_TRY_AGAIN_LATER_BY_GOING_TO_MENU__ATTENDANCE_CHECK);
				return false;
			}

			if(!_owner.hasPremiumAccount() && _loginDelayTask != null && !_loginDelayTask.isDone())
			{
				int receiveDelay = (int) _loginDelayTask.getDelay(TimeUnit.MINUTES) + 1;
				_owner.sendPacket(new SystemMessagePacket(SystemMsg.YOU_CAN_REDEEM_YOUR_REWARD_S1_MINUTES_AFTER_LOGGING_IN_S2_MINUTES_LEFT).addInteger(AFTER_LOGIN_RECEIVE_REWARD_DELAY).addInteger(receiveDelay));
				return false;
			}

			if(_owner.isInventoryFull())
			{
				_owner.sendPacket(SystemMsg.THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHTQUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
				return false;
			}

			_receivedRewardIndex = getNextRewardIndex();

			if(Config.VIP_ATTENDANCE_REWARDS_REWARD_BY_ACCOUNT)
			{
				AccountVariablesDAO.getInstance().insert(_owner.getAccountName(), VIP_ATTENDANCE_REWARD_INDEX_VAR, String.valueOf(getReceivedRewardIndex()));
				AccountVariablesDAO.getInstance().insert(_owner.getAccountName(), VIP_ATTENDANCE_REWARD_DATE_VAR, String.valueOf((int) (System.currentTimeMillis() / 1000L)));
			}
			else
			{
				_owner.setVar(VIP_ATTENDANCE_REWARD_INDEX_VAR, String.valueOf(getReceivedRewardIndex()));
				_owner.setVar(VIP_ATTENDANCE_REWARD_DATE_VAR, String.valueOf((int) (System.currentTimeMillis() / 1000L)));
			}

			if(!_owner.hasPremiumAccount())
				_owner.sendPacket(new SystemMessagePacket(SystemMsg.YOUVE_RECEIVED_YOUR_ATTENDANCE_REWARD_FOR_DAY_S1_).addInteger(getReceivedRewardIndex()));
			else
				_owner.sendPacket(new SystemMessagePacket(SystemMsg.YOUVE_RECEIVED_YOUR_PC_CAF_ATTENDANCE_REWARD_FOR_DAY_S1_).addInteger(getReceivedRewardIndex()));

			_owner.sendPacket(new ExConfirmVipAttendanceCheck(true, getReceivedRewardIndex()));
			ItemFunctions.addItem(_owner, reward.getId(), reward.getCount(), true);
		}
		finally
		{
			writeUnlock();
		}
		startTasks();
		return true;
	}

	public void sendRewardsList(boolean force)
	{
		if(isAvailable())
		{
			if(force || !isReceived())
				_owner.sendPacket(new ExVipAttendanceItemList(_owner));
		}
		else if(force)
			_owner.sendPacket(SystemMsg.YOU_CAN_NO_LONGER_RECEIVE_ATTENDANCE_CHECK_REWARDS_);
	}

	public void onEnterWorld()
	{
		if(isAvailable() && !isReceived())
		{
			_owner.sendPacket(new ExVipAttendanceItemList(_owner));
			if(!_owner.hasPremiumAccount())
				_owner.sendPacket(new SystemMessagePacket(SystemMsg.YOUR_DAY_S1_ATTENDANCE_REWARD_IS_READY_CLICK_ON_THE_REWARDS_ICON).addInteger(getNextRewardIndex()));
			else
				_owner.sendPacket(new SystemMessagePacket(SystemMsg.YOUR_DAY_S1_PC_CAF_ATTENDANCE_REWARD_IS_READY_CLICK_ON_THE_REWARDS_ICON).addInteger(getNextRewardIndex()));
		}
	}

	public void startTasks()
	{
		stopTasks();
		if(isAvailable())
		{
			if(!isReceived())
			{
				long loginDelay = _owner.getOnlineBeginTime() + 1800000 - System.currentTimeMillis();
				if(loginDelay > 0)
					_loginDelayTask = ThreadPoolManager.getInstance().schedule(() -> _owner.sendPacket(SystemMsg.YOU_CAN_REDEEM_YOUR_REWARD_NOW), loginDelay);
			}
			else
			{
				long nextRewardDelay = AttendanceRewards.VIP_ATTENDANCE_DATE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis();
				_getNextRewardIndexTask = ThreadPoolManager.getInstance().schedule(() ->
				{
					restore();
					onEnterWorld();
				}, nextRewardDelay);
			}
		}
	}

	public void stopTasks()
	{
		if(_loginDelayTask != null)
		{
			_loginDelayTask.cancel(false);
			_loginDelayTask = null;
		}

		if(_getNextRewardIndexTask != null)
		{
			_getNextRewardIndexTask.cancel(false);
			_getNextRewardIndexTask = null;
		}
	}

	public void onReceivePremiumAccount()
	{
		restore();
		startTasks();
	}

	public void onRemovePremiumAccount()
	{
		restore();
		startTasks();
	}

	public final void writeLock()
	{
		writeLock.lock();
	}

	public final void writeUnlock()
	{
		writeLock.unlock();
	}

	public final void readLock()
	{
		readLock.lock();
	}

	public final void readUnlock()
	{
		readLock.unlock();
	}
}