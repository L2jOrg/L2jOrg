package ai.others.Spawns;

import ai.AbstractNpcAI;
import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.OnDayNightChange;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public final class EilhalderVonHellmann extends AbstractNpcAI
{
	private static final Logger LOGGER = LoggerFactory.getLogger(EilhalderVonHellmann.class);
	private static final int EILHALDER_VON_HELLMANN = 25328;
	private NpcSpawnTemplate _template;
	
	private EilhalderVonHellmann()
	{
		addSpawnId(EILHALDER_VON_HELLMANN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
	{
		if (event.equals("delete") && (npc != null))
		{
			npc.deleteMe();
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		// Spawn that comes from RaidBossSpawnManager
		if ((npc.getSpawn() == null) || (npc.getSpawn().getNpcSpawnTemplate() == null))
		{
			startQuestTimer("delete", 1000, npc, null);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public void onSpawnNpc(SpawnTemplate template, SpawnGroup group, L2Npc npc)
	{
		LOGGER.info("Spawning Night Raid Boss " + npc.getName());
		DBSpawnManager.getInstance().notifySpawnNightNpc(npc);
	}
	
	@Override
	public void onSpawnDespawnNpc(SpawnTemplate template, SpawnGroup group, L2Npc npc)
	{
		LOGGER.info("Despawning Night Raid Boss " + npc.getName());
	}
	
	@Override
	public void onSpawnActivate(SpawnTemplate template)
	{
		OUT: for (SpawnGroup group : template.getGroups())
		{
			for (NpcSpawnTemplate npc : group.getSpawns())
			{
				if (npc.getId() == EILHALDER_VON_HELLMANN)
				{
					_template = npc;
					break OUT;
				}
			}
		}
		
		handleBoss(GameTimeController.getInstance().isNight());
	}
	
	@RegisterEvent(EventType.ON_DAY_NIGHT_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void onDayNightChange(OnDayNightChange event)
	{
		handleBoss(event.isNight());
	}
	
	/**
	 * @param isNight
	 */
	private void handleBoss(boolean isNight)
	{
		if (_template == null)
		{
			return;
		}
		
		if (isNight)
		{
			_template.spawn(null);
		}
		else
		{
			_template.despawn();
		}
	}
	
	public static AbstractNpcAI provider()
	{
		return new EilhalderVonHellmann();
	}
}
