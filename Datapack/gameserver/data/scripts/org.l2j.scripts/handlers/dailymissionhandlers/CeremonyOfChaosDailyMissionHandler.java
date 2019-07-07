package handlers.dailymissionhandlers;

import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.data.database.data.DailyMissionPlayerData;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.ceremonyofchaos.OnCeremonyOfChaosMatchResult;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

/**
 * @author UnAfraid
 */
public class CeremonyOfChaosDailyMissionHandler extends AbstractDailyMissionHandler
{
	private final int _amount;
	
	public CeremonyOfChaosDailyMissionHandler(DailyMissionDataHolder holder)
	{
		super(holder);
		_amount = holder.getRequiredCompletions();
	}
	
	@Override
	public void init()
	{
		Containers.Global().addListener(new ConsumerEventListener(this, EventType.ON_CEREMONY_OF_CHAOS_MATCH_RESULT, (Consumer<OnCeremonyOfChaosMatchResult>) this::onCeremonyOfChaosMatchResult, this));
	}
	
	private void onCeremonyOfChaosMatchResult(OnCeremonyOfChaosMatchResult event)
	{
		event.getMembers().forEach(member ->
		{
			final DailyMissionPlayerData entry = getPlayerEntry(member.getPlayer(), true);
			if (entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE)
			{
				if (entry.increaseProgress() >= _amount)
				{
					entry.setStatus(DailyMissionStatus.AVAILABLE);
					notifyAvailablesReward(member.getPlayer());
				}
				storePlayerEntry(entry);
			}
		});
	}
}
