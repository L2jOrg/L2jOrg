package handlers.dailymissionhandlers;

import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.model.dailymission.DailyMissionPlayerData;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

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
    public int getProgress(L2PcInstance player)
    {
        return getRequiredCompletition();
    }

    private void onPlayerLevelChanged(OnPlayerLevelChanged event) {
        final L2PcInstance player = event.getActiveChar();
        if ((player.getLevel() >= getRequiredCompletition())) {
            final DailyMissionPlayerData entry = getPlayerEntry(player.getObjectId(), true);
            if (entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE) {
                entry.setStatus(DailyMissionStatus.AVAILABLE);
                storePlayerEntry(entry);
            }
        }
    }
}
