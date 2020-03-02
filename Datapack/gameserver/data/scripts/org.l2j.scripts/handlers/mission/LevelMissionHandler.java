package handlers.mission;

import org.l2j.gameserver.handler.AbstractMissionHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

/**
 * @author Sdw
 */
public class LevelMissionHandler extends AbstractMissionHandler {

    public LevelMissionHandler(MissionDataHolder holder) {
        super(holder);
    }

    @Override
    public void init() {
        Listeners.players().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_LEVEL_CHANGED, (Consumer<OnPlayerLevelChanged>) this::onPlayerLevelChanged, this));
    }

    @Override
    public int getProgress(Player player) {
        return player.getLevel();
    }

    @Override
    public int getStatus(Player player) {
        final var entry = getPlayerEntry(player, true);
        return nonNull(entry) ? entry.getStatus().getClientId() : MissionStatus.NOT_AVAILABLE.getClientId();
    }

    private void onPlayerLevelChanged(OnPlayerLevelChanged event) {
        final Player player = event.getActiveChar();
        if ((player.getLevel() >= getRequiredCompletion())) {
            final MissionPlayerData entry = getPlayerEntry(player, true);
            if (entry.getStatus() == MissionStatus.NOT_AVAILABLE) {
                entry.setStatus(MissionStatus.AVAILABLE);
                storePlayerEntry(entry);
                notifyAvailablesReward(player);
            }
        }
    }
}
