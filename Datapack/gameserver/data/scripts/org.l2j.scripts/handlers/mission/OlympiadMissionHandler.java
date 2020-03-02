package handlers.mission;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.handler.AbstractMissionHandler;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

/**
 * @author UnAfraid
 */
public class OlympiadMissionHandler extends AbstractMissionHandler {
	private final boolean winnerOnly;
	public OlympiadMissionHandler(MissionDataHolder holder) {
		super(holder);
		winnerOnly = holder.getParams().getBoolean("win", false);
	}
	
	@Override
	public void init() {
		Listeners.Global().addListener(new ConsumerEventListener(this, EventType.ON_OLYMPIAD_MATCH_RESULT, (Consumer<OnOlympiadMatchResult>) this::onOlympiadMatchResult, this));
	}
	
	private void onOlympiadMatchResult(OnOlympiadMatchResult event) {
		if (nonNull(event.getWinner())) {
			final MissionPlayerData winnerEntry = getPlayerEntry(event.getWinner().getPlayer(), true);
			increaseProgress(winnerEntry, event.getWinner().getPlayer());
		}
		
		if (nonNull(event.getLoser()) && !winnerOnly) {
			final MissionPlayerData loseEntry = getPlayerEntry(event.getLoser().getPlayer(), true);
			increaseProgress(loseEntry, event.getLoser().getPlayer());
		}
	}

	private void increaseProgress(MissionPlayerData entry, Player player) {
		if (entry.getStatus() == MissionStatus.NOT_AVAILABLE) {
			if (entry.increaseProgress() >= getRequiredCompletion()) {
				entry.setStatus(MissionStatus.AVAILABLE);
				notifyAvailablesReward(player);
			}
			storePlayerEntry(entry);
		}
	}
}
