package org.l2j.gameserver.model.entity.olympiad;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2j.commons.threading.RunnableImpl;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlympiadManager extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadManager.class);

	private Map<Integer, OlympiadGame> _olympiadInstances = new ConcurrentHashMap<Integer, OlympiadGame>();

	public void sleep(long time)
	{
		try
		{
			Thread.sleep(time);
		}
		catch(InterruptedException e)
		{
		}
	}

	@Override
	public void runImpl() throws Exception
	{
		if(Olympiad.isOlympiadEnd())
			return;

		while(Olympiad.inCompPeriod())
		{
			if(Olympiad.getParticipantsMap().isEmpty())
			{
				sleep(60000);
				continue;
			}

			while(Olympiad.inCompPeriod())
			{
				// Подготовка и запуск внеклассовых боев
				if(Olympiad._nonClassBasedRegisters.size() >= Config.NONCLASS_GAME_MIN)
					prepareBattles(CompType.NON_CLASSED, Olympiad._nonClassBasedRegisters);

				// Подготовка и запуск классовых боев
				for(Map.Entry<Integer, List<Integer>> entry : Olympiad._classBasedRegisters.entrySet())
				{
					if(entry.getValue().size() >= Config.CLASS_GAME_MIN)
						prepareBattles(CompType.CLASSED, entry.getValue());
				}

				sleep(30000);
			}

			sleep(30000);
		}

		Olympiad._classBasedRegisters.clear();
		Olympiad._nonClassBasedRegisters.clear();
		Olympiad._playersHWID.clear();

		// when comp time finish wait for all games terminated before execute the cleanup code
		boolean allGamesTerminated = false;

		// wait for all games terminated
		while(!allGamesTerminated)
		{
			sleep(30000);

			if(_olympiadInstances.isEmpty())
				break;

			allGamesTerminated = true;
			for(OlympiadGame game : _olympiadInstances.values())
				if(game.getTask() != null && !game.getTask().isTerminated())
					allGamesTerminated = false;
		}

		_olympiadInstances.clear();
	}

	private void prepareBattles(CompType type, List<Integer> list)
	{
		for(int i = 0; i < Olympiad.STADIUMS.length; i++)
			try
			{
				if(!Olympiad.STADIUMS[i].isFreeToUse())
					continue;

				if(list.size() < 2)
					break;

				int[] nextOpponents = nextOpponents(list, type);
				OlympiadGame game = new OlympiadGame(i, type, nextOpponents[0], nextOpponents[1]);
				game.sheduleTask(new OlympiadGameTask(game, BattleStatus.Begining, 0, 1));

				_olympiadInstances.put(i, game);

				Olympiad.STADIUMS[i].setStadiaBusy();
			}
			catch(Exception e)
			{
				_log.error("", e);
			}
	}

	public void freeOlympiadInstance(int index)
	{
		_olympiadInstances.remove(index);
		Olympiad.STADIUMS[index].setStadiaFree();
	}

	public OlympiadGame getOlympiadInstance(int index)
	{
		return _olympiadInstances.get(index);
	}

	public Map<Integer, OlympiadGame> getOlympiadGames()
	{
		return _olympiadInstances;
	}

	private int[] nextOpponents(List<Integer> list, CompType type)
	{
		int[] opponents = new int[2];

		Integer noble;
		for(int i = 0; i < 2; i++)
		{
			noble = list.remove(Rnd.get(list.size()));
			opponents[i] = noble.intValue();
			removeOpponent(noble);
		}

		return opponents;
	}

	private void removeOpponent(Integer noble)
	{
		Olympiad._classBasedRegisters.removeValue(noble);
		Olympiad._nonClassBasedRegisters.remove(noble);
		Olympiad._playersHWID.remove(noble); //obj id? remove by key
	}
}