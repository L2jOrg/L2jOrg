package l2s.gameserver.instancemanager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import l2s.gameserver.Config;
import l2s.gameserver.dao.AccountVariablesDAO;
import l2s.gameserver.dao.CharacterTrainingCampDAO;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.TrainingCamp;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.network.l2.s2c.ExTrainingZone_Admission;
import l2s.gameserver.network.l2.s2c.ExTrainingZone_Leaving;
import l2s.gameserver.network.l2.s2c.ExUserInfoEquipSlot;
import l2s.gameserver.utils.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrainingCampManager
{
	private class TrainingCampListeners implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			TrainingCamp trainingCamp = getTrainingCamp(player);
			if(trainingCamp == null)
				return;

			if(trainingCamp.isValid(player) && trainingCamp.isTraining())
			{
				int elapsedTime = trainingCamp.getElapsedTime();
				if(elapsedTime < trainingCamp.getMaxDuration())
				{
					onEnterTrainingCamp(player);
					player.startTrainingCampTask(trainingCamp.getRemainingTime() * 1000);
					player.sendPacket(new ExTrainingZone_Admission(trainingCamp.getLevel(), (int) TimeUnit.SECONDS.toMinutes(elapsedTime), trainingCamp.getRemainingTime()));
				}
				else
					trainingCamp.setEndTime(trainingCamp.getStartTime() + TimeUnit.SECONDS.toMillis(trainingCamp.getMaxDuration()));
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(TrainingCampManager.class);

	private static final String TRAINING_CAMP_DURATION_VAR = "@training_camp_duration";

	public static final Location TRAINING_LOCATION = new Location(-56516, 135938, -2672);

	private static TrainingCampManager _instance = new TrainingCampManager();

	private final Map<String, TrainingCamp> _trainingCamps = new HashMap<String, TrainingCamp>();

	public static TrainingCampManager getInstance()
	{
		return _instance;
	}

	public TrainingCampManager()
	{
		//
	}

	public void init()
	{
		if(!Config.TRAINING_CAMP_ENABLE)
			return;

		CharacterTrainingCampDAO.getInstance().restore(_trainingCamps);
		CharListenerList.addGlobal(new TrainingCampListeners());
		_log.info(getClass().getSimpleName() + ": Restored " + _trainingCamps.size() + " players training camps.");
	}

	public boolean addTrainingCamp(Player player, TrainingCamp trainingCamp)
	{
		if(CharacterTrainingCampDAO.getInstance().replace(player.getAccountName(), trainingCamp))
		{
			_trainingCamps.put(player.getAccountName(), trainingCamp);
			return true;
		}
		return false;
	}

	public TrainingCamp getTrainingCamp(Player player)
	{
		return _trainingCamps.get(player.getAccountName());
	}

	public void removeTrainingCamp(Player player)
	{
		if(_trainingCamps.remove(player.getAccountName()) != null)
			CharacterTrainingCampDAO.getInstance().delete(player.getAccountName());
	}

	public int getTrainingCampDuration(String account)
	{
		return Integer.parseInt(AccountVariablesDAO.getInstance().select(account, TRAINING_CAMP_DURATION_VAR, "0"));
	}

	public void addTrainingCampDuration(String account, int value)
	{
		AccountVariablesDAO.getInstance().insert(account, TRAINING_CAMP_DURATION_VAR, String.valueOf(value + getTrainingCampDuration(account)));
	}

	public void refreshTrainingCamp()
	{
		AccountVariablesDAO.getInstance().delete(TRAINING_CAMP_DURATION_VAR);
	}

	public void onEnterTrainingCamp(Player player)
	{
		player.setTarget(null);
		player.stopMove();
		player.removeAutoShots(true);
		player.setStablePoint(player.getLoc());
		player.teleToLocation(TRAINING_LOCATION);
		player.sendPacket(new ExUserInfoEquipSlot(player));
		player.decayMe();
	}

	public void onExitTrainingCamp(Player player)
	{
		player.stopTrainingCampTask();
		player.teleToLocation(player.getStablePoint(), ReflectionManager.MAIN);
		player.sendPacket(new ExUserInfoEquipSlot(player));
		player.setStablePoint(null);
		player.setTarget(null);
		player.stopMove();
		player.spawnMe();
		player.sendPacket(ExTrainingZone_Leaving.STATIC);
	}
}