package handlers.dailymissionhandlers;

import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.data.database.data.DailyMissionPlayerData;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

/**
 * @author Sdw
 */
public class LevelDailyMissionHandler extends AbstractDailyMissionHandler {

    public LevelDailyMissionHandler(DailyMissionDataHolder holder) {
        super(holder);
    }

    @Override
    public void init() {
        Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_LEVEL_CHANGED, (Consumer<OnPlayerLevelChanged>) this::onPlayerLevelChanged, this));
    }

    @Override
    public int getProgress(Player player) {
        return player.getLevel();
    }

    @Override
    public int getStatus(Player player) {
        final var entry = getPlayerEntry(player, true);
        return nonNull(entry) ? entry.getStatus().getClientId() : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    private void onPlayerLevelChanged(OnPlayerLevelChanged event) {
        final Player player = event.getActiveChar();
        if ((player.getLevel() >= getRequiredCompletion())) {
            final DailyMissionPlayerData entry = getPlayerEntry(player, true);
            if (entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE) {
                entry.setStatus(DailyMissionStatus.AVAILABLE);
                storePlayerEntry(entry);
                notifyAvailablesReward(player);
            }
        }
    }
}
