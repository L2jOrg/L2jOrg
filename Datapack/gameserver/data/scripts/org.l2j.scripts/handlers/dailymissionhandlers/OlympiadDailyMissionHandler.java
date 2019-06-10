package handlers.dailymissionhandlers;

import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.model.dailymission.DailyMissionPlayerData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author UnAfraid
 */
public class OlympiadDailyMissionHandler extends AbstractDailyMissionHandler {
	private final boolean winnerOnly;
	public OlympiadDailyMissionHandler(DailyMissionDataHolder holder) {
		super(holder);
		winnerOnly = holder.getParams().getBoolean("win", false);
	}
	
	@Override
	public void init() {
		Containers.Global().addListener(new ConsumerEventListener(this, EventType.ON_OLYMPIAD_MATCH_RESULT, (Consumer<OnOlympiadMatchResult>) this::onOlympiadMatchResult, this));
	}
	
	private void onOlympiadMatchResult(OnOlympiadMatchResult event) {
		if (nonNull(event.getWinner())) {
			final DailyMissionPlayerData winnerEntry = getPlayerEntry(event.getWinner().getObjectId(), true);
			increaseProgress(winnerEntry);
		}
		
		if (nonNull(event.getLoser()) && !winnerOnly) {
			final DailyMissionPlayerData loseEntry = getPlayerEntry(event.getLoser().getObjectId(), true);
			increaseProgress(loseEntry);
		}
	}

	private void increaseProgress(DailyMissionPlayerData entry) {
		if (entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE) {
			if (entry.increaseProgress() >= getRequiredCompletition()) {
				entry.setStatus(DailyMissionStatus.AVAILABLE);
			}
			storePlayerEntry(entry);
		}
	}
}
