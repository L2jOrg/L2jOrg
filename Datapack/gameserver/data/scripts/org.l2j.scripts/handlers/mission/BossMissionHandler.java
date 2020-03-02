package handlers.mission;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.handler.AbstractMissionHandler;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.util.MathUtil;

import java.util.List;

/**
 * @author UnAfraid
 */
public class BossMissionHandler extends AbstractMissionHandler
{
	private final int _amount;
	
	public BossMissionHandler(MissionDataHolder holder)
	{
		super(holder);
		_amount = holder.getRequiredCompletions();
	}
	
	@Override
	public void init()
	{
		Listeners.Monsters().addListener(new ConsumerEventListener(this, EventType.ON_ATTACKABLE_KILL, (OnAttackableKill event) -> onAttackableKill(event), this));
	}

	
	private void onAttackableKill(OnAttackableKill event)
	{
		final Attackable monster = event.getTarget();
		final Player player = event.getAttacker();
		if (monster.isRaid() && (monster.getInstanceId() > 0) && (player != null))
		{
			final Party party = player.getParty();
			if (party != null)
			{
				final CommandChannel channel = party.getCommandChannel();
				final List<Player> members = channel != null ? channel.getMembers() : party.getMembers();
				members.stream().filter(member -> MathUtil.isInsideRadius3D(member, monster, Config.ALT_PARTY_RANGE)).forEach(this::processPlayerProgress);
			}
			else
			{
				processPlayerProgress(player);
			}
		}
	}
	
	private void processPlayerProgress(Player player)
	{
		final MissionPlayerData entry = getPlayerEntry(player, true);
		if (entry.getStatus() == MissionStatus.NOT_AVAILABLE)
		{
			if (entry.increaseProgress() >= _amount)
			{
				entry.setStatus(MissionStatus.AVAILABLE);
			}
			storePlayerEntry(entry);
		}
	}
}
