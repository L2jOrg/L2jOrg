package l2s.gameserver.listener.hooks;

import java.util.*;

import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public class ListenerHook
{
	protected void addHookNpc(ListenerHookType type, int npcId)
	{
		try
		{
			NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
			if(template != null)
				template.addListenerHook(type, this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void addHookNpc(ListenerHookType type, int... npcIds)
	{
		for(int npcId : npcIds)
			addHookNpc(type, npcId);
	}

	protected void addHookPlayer(ListenerHookType type, Player player)
	{
		player.addListenerHook(type, this);
	}

	protected void removeHookPlayer(ListenerHookType type, Player player)
	{
		player.removeListenerHookType(type, this);
	}

	protected void addHookGlobal(ListenerHookType type)
	{
		addGlobalListenerHookType(type, this);
	}

	public void onNpcKill(NpcInstance npc, Player killer)
	{}

	public void onNpcAttack(NpcInstance npc, int damage, Player attacker)
	{}

	public void onNpcAsk(NpcInstance npc, int ask, long reply, Player player)
	{}

	public boolean onNpcFirstTalk(NpcInstance npc, Player player)
	{
		return false;
	}

	public void onNpcSpawn(NpcInstance npc)
	{}

	public void onNpcDespawn(NpcInstance npc)
	{}

	public void onPlayerFinishCastSkill(Player player, int skillId)
	{}

	public void onPlayerDie(Player player, Creature killer)
	{}

	public void onPlayerEnterGame(Player player)
	{}

	public void onPlayerQuitGame(Player player)
	{}

	public void onPlayerTeleport(Player player, int reflectionId)
	{}

	private static Map<ListenerHookType, Set<ListenerHook>> _globalListenerHooks = new HashMap<ListenerHookType, Set<ListenerHook>>();

	public void onPlayerCreate(Player player)
	{}

	private static void addGlobalListenerHookType(ListenerHookType type, ListenerHook hook)
	{
		Set<ListenerHook> hooks = _globalListenerHooks.get(type);
		if(hooks == null)
		{
			hooks = new HashSet<ListenerHook>();
			_globalListenerHooks.put(type, hooks);
		}
		hooks.add(hook);
	}

	public static Set<ListenerHook> getGlobalListenerHooks(ListenerHookType type)
	{
		Set<ListenerHook> hooks = _globalListenerHooks.get(type);
		if(hooks == null)
			return Collections.emptySet();
		return hooks;
	}

	public void onGlobalBbs(String command, Player player)
	{}

	public void onPlayerGlobalLevelUp(Player player, int oldLevel, int newLevel)
	{}

	public void onPlayerGlobalDie(Player player)
	{}

	public void onPlayerGlobalFriendAdd(Player player)
	{}

	public void onPlayerGlobalItemAdd(Player player, int itemId, long count)
	{}

	public void onPlayerGlobalKill(Player player, Player killer)
	{}

	public void onPlayerGlobalPvPUp(Player player, int oldPvP)
	{}

	public void onPlayerGlobalPKUp(Player player, int oldPK)
	{}

	public void onPlayerGlobalStartCastleSiegeInClan(int objectId)
	{}

	public void onPlayerGlobalTakeCastle(Player player)
	{}

	public void onPlayerGlobalTakeDamage(Player player, Creature attacker)
	{}
}