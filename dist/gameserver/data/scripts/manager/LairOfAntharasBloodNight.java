package manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SimpleSpawner;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.Location;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.utils.ReflectionUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author me
 * @date 0:08/12.06.2017
 * TODO: Переделать.
 */
public class LairOfAntharasBloodNight implements OnInitScriptListener
{
	public static class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(!self.isNpc() || killer == null || !killer.isPlayable())
				return; //shouldn't happen

			if(self.getNpcId() != NPC_2 && self.getNpcId() != NPC_1)
				return;
			
			NpcInstance npc = (NpcInstance) self; //must be npc from the list
			if(_list.contains(npc.getSpawn()) || _list2.contains(npc.getSpawn()))
			{
				int count = Rnd.get(1, 3);
				for(int i = 0; i < count ; i++) 
				{
					int[] drop = (npc.getNpcId() == NPC_1 ? REWARD_LOW : REWARD_HI);
					npc.dropItem(killer.getPlayer(), drop[Rnd.get(drop.length)], 1); //must be a playable otherwise see errors (shouldn't happen)
				}
				_list2.remove(npc);
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(LairOfAntharasBloodNight.class);

	private static Zone _zone;
	private static int NPC_1 = 18015;
	private static int NPC_2 = 18007;
	private static int[] PVP_HOURS = {20,21,22,23};
	private static int[] PVP_HOURS_RB = {20,22};
	private static int[] REWARD_LOW = {49762, 49763};
	private static int[] REWARD_HI = {49760, 49761};
	private static List<SimpleSpawner> _list = new ArrayList<SimpleSpawner>();
	private static Collection<SimpleSpawner> _list2 = new ArrayList<SimpleSpawner>();
	private static final OnDeathListener DEATH_LISTENER = new DeathListener();

	@Override
	public void onInit()
	{
		_list.clear();

		createNpc(new Location(140172, 114128, -3735, 14091));
		createNpc(new Location(148888, 115683, -3733, 27220));
		createNpc(new Location(153792, 121573, -3819, 50891));
		createNpc(new Location(153800, 119052, -3819, 20439));
		createNpc(new Location(152496, 119329, -3784, 6848));
		createNpc(new Location(147728, 112277, -3735, 37546));
		createNpc(new Location(153712, 108544, -5167, 39384));
		createNpc(new Location(149886, 109687, -5192, 64701));
		createNpc(new Location(150236, 112551, -5504, 16119));
		createNpc(new Location(153595, 112234, -5535, 43067));
		createNpc(new Location(150532, 114665, -5487, 34290));
		createNpc(new Location(153785, 116952, -5269, 9559));
		createNpc(new Location(150058, 121050, -4877, 32767));
		createNpc(new Location(144983, 117695, -3927, 14661));
		createNpc(new Location(145794, 119521, -3927, 17736));
		createNpc(new Location(143016, 117265, -3927, 23386));
		createNpc(new Location(142694, 118916, -3927, 34490));
		createNpc(new Location(141109, 121881, -3927, 54223));
		createNpc(new Location(142980, 121411, -3927, 45532));
		createNpc(new Location(140517, 118174, -3927, 57262));
		createNpc(new Location(148151, 117786, -3726, 38958));
		createNpc(new Location(144353, 114644, -3704, 4750));
		createNpc(new Location(143511, 112309, -3959, 34784));
		createNpc(new Location(148261, 109911, -3928, 34088));
		createNpc(new Location(144480, 108542, -3959, 56205));
		createNpc(new Location(143146, 108702, -3959, 11331));
		createNpc(new Location(142430, 107224, -3959, 63667));
		createNpc(new Location(141159, 109822, -3959, 40543));

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);

		while(cal.getTimeInMillis() < System.currentTimeMillis())
			cal.add(Calendar.MINUTE, 30);

		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> spawn(), cal.getTimeInMillis() - System.currentTimeMillis(), 30 * 60 * 1000);

		_zone = ReflectionUtils.getZone("[lair_of_anthars_pvp]");
		CharListenerList.addGlobal(DEATH_LISTENER);
		_log.info("Loa Blood list "+_list.size()+" spawn points init spawn time "+cal.getTime()+" , zone "+(_zone != null ? "exists" : " NOT EXISTS!"));
	}

	private static synchronized void createNpc(Location loc)
	{
		NpcTemplate template = NpcHolder.getInstance().getTemplate(NPC_1);
		SimpleSpawner spawn = new SimpleSpawner(template);
		spawn.setLoc(loc);
		spawn.setAmount(1);
		spawn.setRespawnDelay(0);
		spawn.setReflection(ReflectionManager.MAIN);
		_list.add(spawn); 
		//npc.spawnMe(npc.getSpawnedLoc());	that's just a reminder
	}

	private static void spawn()
	{
		if(_list.isEmpty()) 
			return;

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		int count = 0;

		if(!_list2.isEmpty())
		{
			for(SimpleSpawner npc : _list2)
			{
				if(npc.getSpawnedCount() > 0)
					npc.getFirstSpawned().deleteMe();
			}
			_list2.clear();
		}

		if(ArrayUtils.contains(PVP_HOURS, cal.get(Calendar.HOUR_OF_DAY)) && cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
		{
			Announcements.announceToAll("LoA Standard PvP Event Started!");
			ExShowScreenMessage sm = new ExShowScreenMessage("LoA and TOI Standard PvP Event Started!", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
			for(Player player : GameObjectsStorage.getPlayers())
				player.sendPacket(sm);
			//PvP-event
			if(_zone != null && !_zone.isActive())
			{
				_zone.setActive(true);
			}

			int[] index = new int[Rnd.get(3, 4)];
			if(ArrayUtils.contains(PVP_HOURS_RB, cal.get(Calendar.HOUR_OF_DAY)) && cal.get(Calendar.MINUTE) == 30)
			{
				for(int i = 0; i < index.length; i++)
					index[i] = Rnd.get(_list.size());
			}
			else
			{
				for(int i = 0; i < index.length; i++)
					index[i] = -1;
			}

			for(int i = 0; i < _list.size(); i++)
			{
				SimpleSpawner npc = _list.get(i);
				Location loc = npc.getFirstSpawned().getSpawnedLoc();
				if(npc != null)
				{
					if(npc.getSpawnedCount() > 0)
						npc.getFirstSpawned().deleteMe();

					if(ArrayUtils.contains(index, i))
					{
						NpcTemplate template = NpcHolder.getInstance().getTemplate(NPC_2);
						SimpleSpawner spawn = new SimpleSpawner(template);
						spawn.setLoc(loc);
						spawn.setAmount(1);
						spawn.setRespawnDelay(0);
						spawn.setReflection(ReflectionManager.MAIN);
						spawn.init();
						spawn.stopRespawn();
						_list2.add(spawn);
						//_log.info(" npcId "+npc2.getNpcId()+" spawned PvP-event at "+npc2.getSpawnedLoc().toXYZString());
						count++;
					}
					else
					{
						npc.init();
						npc.stopRespawn();
						//npc3.getSpawn().stopRespawn();
						//_log.info(" npcId "+spawn.getNpcId()+" spawned PvP-event at "+spawn.getLastSpawn().getLocation().toXYZString());
						count++;
					}
				}
			}
		}
		else
		{
			//no PvP-event
			if(_zone != null && _zone.isActive())
			{
				_zone.setActive(false);
			}

			int mobCount = Rnd.get(2, 3);
			for(int i = 0; i < mobCount; i++)
			{
				SimpleSpawner npc = _list.get(Rnd.get(_list.size()));
				if(npc != null)
				{
					if(npc.getSpawnedCount() > 0)
						npc.getFirstSpawned().deleteMe();

					npc.init();
					npc.stopRespawn();
					//_log.info(" npcId "+spawn.getNpcId()+" spawned at "+spawn.getLastSpawn().getLocation().toXYZString());
					count++;
				}
			}
		}
		_log.info("spawn time "+cal.getTime()+" mob count "+count+" hour "+cal.get(Calendar.HOUR_OF_DAY));
	}
}
