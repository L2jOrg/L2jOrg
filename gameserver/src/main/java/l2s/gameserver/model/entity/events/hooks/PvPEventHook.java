package l2s.gameserver.model.entity.events.hooks;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.PvPEvent;
import l2s.gameserver.model.entity.events.objects.PvPEventArenaObject;
import l2s.gameserver.model.entity.events.objects.PvPEventPlayerObject;
import l2s.gameserver.model.entity.events.objects.RewardObject;
import l2s.gameserver.utils.ItemFunctions;

public class PvPEventHook extends ListenerHook
{
	private static PvPEventHook ourInstance = new PvPEventHook();

	public static PvPEventHook getInstance()
	{
		return ourInstance;
	}

	private PvPEventHook()
	{
	}

	@Override
	public void onPlayerQuitGame(Player player)
	{
		List<PvPEvent> events = EventHolder.getInstance().getEvents(PvPEvent.class);
		for(PvPEvent event : events)
			event.removeObject("registered_players", player);
	}

	@Override
	public void onPlayerDie(Player player, Creature killer)
	{
		PvPEvent event = player.getEvent(PvPEvent.class);
		if(event == null)
			return;

		PvPEventArenaObject arena = event.getArena(player);
		if(arena == null)
			return;

		if(arena != player.getReflection())
			return;

		final PvPEventPlayerObject member = arena.getParticipant(player);
		if(member == null)
			return;

		member.addCountDie();
		if(event.getCountDieFromExit() != -1 && member.getCountDie() >= event.getCountDieFromExit())
			arena.removePlayer(player);
		else
		{
			ThreadPoolManager.getInstance().schedule(() ->
			{
				int teamId = member.getTeam();
				if(teamId == -1)
					teamId = 0;

				member.setTeleport(true);
				arena.teleportPlayer(player, event.getLocation("team" + teamId));
				arena.buff(player);
				arena.heal(player);
			}, 3000);
		}
		if(event.checkStop())
			arena.check();

		if(!(killer instanceof Playable))
			return;

		if(member.getTeam() != -1 && player.getTeam() == killer.getTeam())
			return;

		PvPEventPlayerObject killerMember = event.getParticipant(killer.getPlayer());
		if(killerMember != null)
		{
			killerMember.addPoint();
			if(event.isIncPvP())
				killer.getPlayer().setPvpKills(killer.getPlayer().getPvpKills() + 1);
		}
		List<RewardObject> rewards = event.getObjects("reward_for_kill");
		rewards.stream().filter(reward -> Rnd.chance(reward.getChance())).forEach(reward ->
		{
			ItemFunctions.addItem(killer.getPlayer(), reward.getItemId(), Rnd.get(reward.getMinCount(), reward.getMaxCount()) * (long) (killer.getPlayer().hasPremiumAccount() ? event.getModRewardForPremium() : 1));
		});
	}

	@Override
	public void onPlayerTeleport(Player player, int reflectionId)
	{
		PvPEvent event = player.getEvent(PvPEvent.class);
		if(event == null)
			return;

		PvPEventArenaObject arena = event.getArena(player);
		if(arena == null)
			return;

		if(arena == player.getReflection())
			return;

		arena.removePlayer(player);
	}
}