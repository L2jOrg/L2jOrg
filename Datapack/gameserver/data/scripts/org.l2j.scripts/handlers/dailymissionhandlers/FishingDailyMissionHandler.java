package handlers.dailymissionhandlers;

import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.data.database.data.DailyMissionPlayerData;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerFishing;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

/**
 * @author UnAfraid
 */
public class FishingDailyMissionHandler extends AbstractDailyMissionHandler {
	
	public FishingDailyMissionHandler(DailyMissionDataHolder holder) {
		super(holder);
	}
	
	@Override
	public void init() {
		Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_FISHING, (Consumer<OnPlayerFishing>) this::onPlayerFishing, this));
	}
	
	private void onPlayerFishing(OnPlayerFishing event) {
		final L2PcInstance player = event.getActiveChar();
		final DailyMissionPlayerData entry = getPlayerEntry(player, true);
		if (entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE) {
			if (entry.increaseProgress() >= getRequiredCompletion()) {
				entry.setStatus(DailyMissionStatus.AVAILABLE);
				notifyAvailablesReward(player);
			}
			storePlayerEntry(entry);
		}

	}
}
