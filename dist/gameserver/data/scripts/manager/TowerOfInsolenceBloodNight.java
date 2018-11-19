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
public class TowerOfInsolenceBloodNight implements OnInitScriptListener
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

	private static final Logger _log = LoggerFactory.getLogger(TowerOfInsolenceBloodNight.class);

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

		createNpc(new Location(112897, 17234, -655, 7809));
		createNpc(new Location(112955, 14454, -655, 50808));
		createNpc(new Location(116114, 14460, -655, 47371));
		createNpc(new Location(116272, 17140, -655, 5022));
		createNpc(new Location(113977, 13748, 918, 64381));
		createNpc(new Location(115910, 15962, 918, 55313));
		createNpc(new Location(113486, 15137, 918, 54153));
		createNpc(new Location(115350, 18467, 918, 30773));
		createNpc(new Location(113240, 16027, 1937, 16286));
		createNpc(new Location(114931, 14443, 1937, 36454));
		createNpc(new Location(114758, 17378, 1937, 17337));
		createNpc(new Location(114659, 16024, 1937, 29938));
		createNpc(new Location(113653, 18445, 2947, 2634));
		createNpc(new Location(115227, 14745, 2947, 12920));
		createNpc(new Location(113217, 15830, 2947, 4117));
		createNpc(new Location(115312, 17336, 2947, 36336));
		createNpc(new Location(114655, 15996, 3957, 11593));
		createNpc(new Location(115833, 15425, 3957, 10945));
		createNpc(new Location(115163, 17320, 3957, 31293));
		createNpc(new Location(114173, 14592, 3957, 19810));
		createNpc(new Location(113195, 15565, 4967, 6168));
		createNpc(new Location(115871, 14973, 4967, 21954));
		createNpc(new Location(115328, 17459, 4967, 48737));
		createNpc(new Location(114625, 15993, 5008, 49191));
		createNpc(new Location(112239, 15212, 5977, 14903));
		createNpc(new Location(114671, 15973, 5977, 39714));
		createNpc(new Location(116493, 17934, 5977, 52395));
		createNpc(new Location(114933, 13480, 5977, 64367));
		createNpc(new Location(115881, 14814, 6987, 24316));
		createNpc(new Location(113336, 17294, 6987, 57058));
		createNpc(new Location(115077, 14178, 6987, 20663));
		createNpc(new Location(114357, 17909, 6987, 56447));

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);

		while(cal.getTimeInMillis() < System.currentTimeMillis())
			cal.add(Calendar.MINUTE, 30);

		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> spawn(), cal.getTimeInMillis() - System.currentTimeMillis(), 30 * 60 * 1000);

		_zone = ReflectionUtils.getZone("[toi_pvp]");
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

			int[] floors = new int[Rnd.get(3, 4)];
			int[] index = new int[floors.length];
			if(ArrayUtils.contains(PVP_HOURS_RB, cal.get(Calendar.HOUR_OF_DAY)) && cal.get(Calendar.MINUTE) == 30)
			{
				for(int i = 0; i < floors.length; i++)
				{
					int idx = Rnd.get(4, 11);
					while(ArrayUtils.contains(floors, idx))
						idx = Rnd.get(4, 11);

					floors[i] = idx;
					//_log.info(qn+" floors "+floors[i]+" i "+i);
				}
				//4 mobs by floor
				for(int i = 0; i < floors.length; i++)
				{
					index[i] = Rnd.get((floors[i] - 4) * 4, (floors[i] - 4 + 3) * 4);
					//_log.info(qn+" index "+index[i]+" from "+((floors[i] - 4) * 4)+" to "+((floors[i] - 4 + 3) * 4));
				}
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
