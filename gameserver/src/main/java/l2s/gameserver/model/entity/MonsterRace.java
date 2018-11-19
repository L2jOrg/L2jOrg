package l2s.gameserver.model.entity;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.NpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonsterRace
{
	private static final Logger _log = LoggerFactory.getLogger(MonsterRace.class);

	private NpcInstance[] monsters;
	private static MonsterRace _instance;
	private int[][] speeds;
	private int[] first, second;

	private MonsterRace()
	{
		monsters = new NpcInstance[8];
		speeds = new int[8][20];
		first = new int[2];
		second = new int[2];
	}

	public static MonsterRace getInstance()
	{
		if(_instance == null)
			_instance = new MonsterRace();
		return _instance;
	}

	public void newRace()
	{
		int random = 0;

		for(int i = 0; i < 8; i++)
		{
			int id = 31003;
			random = Rnd.get(24);
			for(int j = i - 1; j >= 0; j--)
            {
                if(monsters[j].getNpcId() == id + random)
                    random = Rnd.get(24);
            }
			try
			{
                monsters[i] = NpcUtils.newInstance(id + random);
			}
			catch(Exception e)
			{
				_log.error("", e);
			}
		}
		newSpeeds();
	}

	public void newSpeeds()
	{
		speeds = new int[8][20];
		int total = 0;
		first[1] = 0;
		second[1] = 0;
		for(int i = 0; i < 8; i++)
		{
			total = 0;
			for(int j = 0; j < 20; j++)
			{
				if(j == 19)
					speeds[i][j] = 100;
				else
					speeds[i][j] = Rnd.get(65, 124);
				total += speeds[i][j];
			}
			if(total >= first[1])
			{
				second[0] = first[0];
				second[1] = first[1];
				first[0] = 8 - i;
				first[1] = total;
			}
			else if(total >= second[1])
			{
				second[0] = 8 - i;
				second[1] = total;
			}
		}
	}

	/**
	 * @return Returns the monsters.
	 */
	public NpcInstance[] getMonsters()
	{
		return monsters;
	}

	/**
	 * @return Returns the speeds.
	 */
	public int[][] getSpeeds()
	{
		return speeds;
	}

	public int getFirstPlace()
	{
		return first[0];
	}

	public int getSecondPlace()
	{
		return second[0];
	}
}