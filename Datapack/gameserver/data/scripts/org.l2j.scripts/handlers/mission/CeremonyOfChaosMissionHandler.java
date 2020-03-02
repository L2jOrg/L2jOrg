package handlers.mission;

import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.handler.AbstractMissionHandler;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.ceremonyofchaos.OnCeremonyOfChaosMatchResult;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

/**
 * @author UnAfraid
 */
public class CeremonyOfChaosMissionHandler extends AbstractMissionHandler
{
	private final int _amount;
	
	public CeremonyOfChaosMissionHandler(MissionDataHolder holder)
	{
		super(holder);
		_amount = holder.getRequiredCompletions();
	}
	
	@Override
	public void init()
	{
		Listeners.Global().addListener(new ConsumerEventListener(this, EventType.ON_CEREMONY_OF_CHAOS_MATCH_RESULT, (Consumer<OnCeremonyOfChaosMatchResult>) this::onCeremonyOfChaosMatchResult, this));
	}
	
	private void onCeremonyOfChaosMatchResult(OnCeremonyOfChaosMatchResult event)
	{
		event.getMembers().forEach(member ->
		{
			final MissionPlayerData entry = getPlayerEntry(member.getPlayer(), true);
			if (entry.getStatus() == MissionStatus.NOT_AVAILABLE)
			{
				if (entry.increaseProgress() >= _amount)
				{
					entry.setStatus(MissionStatus.AVAILABLE);
					notifyAvailablesReward(member.getPlayer());
				}
				storePlayerEntry(entry);
			}
		});
	}
}
