package handlers.mission;

import org.l2j.gameserver.handler.AbstractMissionHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerFishing;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

/**
 * @author UnAfraid
 */
public class FishingMissionHandler extends AbstractMissionHandler {
	
	public FishingMissionHandler(MissionDataHolder holder) {
		super(holder);
	}
	
	@Override
	public void init() {
		Listeners.players().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_FISHING, (Consumer<OnPlayerFishing>) this::onPlayerFishing, this));
	}
	
	private void onPlayerFishing(OnPlayerFishing event) {
		final Player player = event.getActiveChar();
		final MissionPlayerData entry = getPlayerEntry(player, true);
		if (entry.getStatus() == MissionStatus.NOT_AVAILABLE) {
			if (entry.increaseProgress() >= getRequiredCompletion()) {
				entry.setStatus(MissionStatus.AVAILABLE);
				notifyAvailablesReward(player);
			}
			storePlayerEntry(entry);
		}

	}
}
