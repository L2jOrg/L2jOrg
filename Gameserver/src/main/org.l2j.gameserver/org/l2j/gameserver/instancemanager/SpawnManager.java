package org.l2j.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.data.dao.SpawnsDAO;
import org.l2j.gameserver.data.xml.holder.NpcHolder;
import org.l2j.gameserver.data.xml.holder.SpawnHolder;
import org.l2j.gameserver.listener.game.OnDayNightChangeListener;
import org.l2j.gameserver.model.HardSpawner;
import org.l2j.gameserver.model.Spawner;
import org.l2j.gameserver.model.instances.MonsterInstance;
import org.l2j.gameserver.model.instances.SaveableMonsterInstance;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.templates.spawn.PeriodOfDay;
import org.l2j.gameserver.templates.spawn.SpawnTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class SpawnManager
{
	private class Listeners implements OnDayNightChangeListener
	{
		@Override
		public void onDay()
		{
			despawn(PeriodOfDay.NIGHT.name());
			spawn(PeriodOfDay.DAY.name());
		}

		@Override
		public void onNight()
		{
			despawn(PeriodOfDay.DAY.name());
			spawn(PeriodOfDay.NIGHT.name());
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(SpawnManager.class);

	private static SpawnManager _instance = new SpawnManager();

	private Map<String, List<Spawner>> _spawns = new ConcurrentHashMap<String, List<Spawner>>();
	private Listeners _listeners = new Listeners();

	public static SpawnManager getInstance()
	{
		return _instance;
	}

	private SpawnManager()
	{
		for(Map.Entry<String, List<SpawnTemplate>> entry : SpawnHolder.getInstance().getSpawns().entrySet())
			fillSpawn(entry.getKey(), entry.getValue());

		fillSpawn("NONE", SpawnsDAO.getInstance().restore());

		GameTimeController.getInstance().addListener(_listeners);
	}

	public List<Spawner> fillSpawn(String group, List<SpawnTemplate> templateList)
	{
		if(Config.DONTLOADSPAWN)
			return Collections.emptyList();

		List<Spawner> spawnerList = _spawns.get(group);
		if(spawnerList == null)
			_spawns.put(group, spawnerList = new ArrayList<Spawner>(templateList.size()));

		for(SpawnTemplate template : templateList)
		{
			HardSpawner spawner = new HardSpawner(template);
			spawnerList.add(spawner);

			NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(spawner.getMainNpcId());

			var serverSettings = getSettings(ServerSettings.class);
            boolean saveable = npcTemplate.isRaid || npcTemplate.isInstanceOf(SaveableMonsterInstance.class);

            if(serverSettings.rateMobSpawn() > 1 && npcTemplate.isInstanceOf(MonsterInstance.class) && !saveable && npcTemplate.level >= serverSettings.rateMobSpawnMinLevel() && npcTemplate.level <= serverSettings.rateMobSpawnMaxLevel())
				spawner.setAmount((int) (template.getCount() * serverSettings.rateMobSpawn()));
			else
				spawner.setAmount(template.getCount());

			spawner.setRespawnDelay(template.getRespawn(), template.getRespawnRandom());
            spawner.setRespawnPattern(template.getRespawnPattern());
			spawner.setReflection(ReflectionManager.MAIN);
			spawner.setRespawnTime(0);
			if(saveable && group.equals(PeriodOfDay.NONE.name()))
				RaidBossSpawnManager.getInstance().addNewSpawn(npcTemplate.getId(), spawner);
		}

		return spawnerList;
	}

	public void spawnAll()
	{
		spawn(PeriodOfDay.NONE.name());
		if(Config.ALLOW_EVENT_GATEKEEPER)
			spawn("event_gatekeeper");
        if(Config.SPAWN_VITAMIN_MANAGER)
           spawn("vitamin_manager");
        if(!Config.ALLOW_CLASS_MASTERS_LIST.isEmpty())
           spawn("class_master");
        if(Config.ENABLE_OLYMPIAD)
            spawn("olympiad");
		if(Config.TRAINING_CAMP_ENABLE)
			spawn("training_camp");
	}

    public void despawnAll()
    {
        RaidBossSpawnManager.getInstance().cleanUp();

        for(List<Spawner> spawnerList : _spawns.values())
            for(Spawner spawner : spawnerList)
                spawner.deleteAll();
    }

	public List<Spawner> spawn(String group, boolean logging)
	{
		List<Spawner> spawnerList = _spawns.get(group);
		if(spawnerList == null)
            return Collections.emptyList();

		int npcSpawnCount = 0;

		for(Spawner spawner : spawnerList)
		{
			npcSpawnCount += spawner.init();

			if(logging && npcSpawnCount % 1000 == 0 && npcSpawnCount != 0)
				_log.info("SpawnManager: spawned " + npcSpawnCount + " npc for group: " + group);
		}
        if(logging)
		    _log.info("SpawnManager: spawned " + npcSpawnCount + " npc; spawns: " + spawnerList.size() + "; group: " + group);
        return spawnerList;
	}

    public List<Spawner> spawn(String group)
    {
        return spawn(group, true);
    }

	public void despawn(String group)
	{
		List<Spawner> spawnerList = _spawns.get(group);
		if(spawnerList == null)
			return;

		for(Spawner spawner : spawnerList)
			spawner.deleteAll();
	}

	public List<Spawner> getSpawners(String group)
	{
		List<Spawner> list = _spawns.get(group);
		return list == null ? Collections.<Spawner>emptyList() : list;
	}

	public void reloadAll()
	{
        despawnAll();

		RaidBossSpawnManager.getInstance().reloadBosses();

		spawnAll();

		if(GameTimeController.getInstance().isNowNight())
			_listeners.onNight();
		else
			_listeners.onDay();
	}
}