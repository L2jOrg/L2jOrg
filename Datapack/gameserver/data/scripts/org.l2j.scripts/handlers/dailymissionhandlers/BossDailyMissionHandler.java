package handlers.dailymissionhandlers;

import java.util.List;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.data.database.data.DailyMissionPlayerData;
import org.l2j.gameserver.model.L2CommandChannel;
import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

/**
 * @author UnAfraid
 */
public class BossDailyMissionHandler extends AbstractDailyMissionHandler
{
	private final int _amount;
	
	public BossDailyMissionHandler(DailyMissionDataHolder holder)
	{
		super(holder);
		_amount = holder.getRequiredCompletions();
	}
	
	@Override
	public void init()
	{
		Containers.Monsters().addListener(new ConsumerEventListener(this, EventType.ON_ATTACKABLE_KILL, (OnAttackableKill event) -> onAttackableKill(event), this));
	}

	
	private void onAttackableKill(OnAttackableKill event)
	{
		final L2Attackable monster = event.getTarget();
		final L2PcInstance player = event.getAttacker();
		if (monster.isRaid() && (monster.getInstanceId() > 0) && (player != null))
		{
			final L2Party party = player.getParty();
			if (party != null)
			{
				final L2CommandChannel channel = party.getCommandChannel();
				final List<L2PcInstance> members = channel != null ? channel.getMembers() : party.getMembers();
				members.stream().filter(member -> member.calculateDistance3D(monster) <= Config.ALT_PARTY_RANGE).forEach(this::processPlayerProgress);
			}
			else
			{
				processPlayerProgress(player);
			}
		}
	}
	
	private void processPlayerProgress(L2PcInstance player)
	{
		final DailyMissionPlayerData entry = getPlayerEntry(player, true);
		if (entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE)
		{
			if (entry.increaseProgress() >= _amount)
			{
				entry.setStatus(DailyMissionStatus.AVAILABLE);
			}
			storePlayerEntry(entry);
		}
	}
}
